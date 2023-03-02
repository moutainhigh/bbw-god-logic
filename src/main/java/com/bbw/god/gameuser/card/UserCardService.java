package com.bbw.god.gameuser.card;

import com.bbw.cache.UserCacheService;
import com.bbw.coder.CoderNotify;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.server.god.GodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡牌服务工具类
 *
 * @author suhq
 * @date 2018年11月24日 下午8:16:45
 */
@Slf4j
@Service
public class UserCardService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodService godService;
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private UserCacheService userCacheService;
    // 城池选牌策略ID
    private static final String[] CITY_STRATEGY_KEYS = {RandomKeys.CITY_CARD_1, RandomKeys.CITY_CARD_2, RandomKeys.CITY_CARD_3, RandomKeys.CITY_CARD_4, RandomKeys.CITY_CARD_5};

    /**
     * 获取玩家所有卡牌记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    public List<UserCard> getUserCards(long uid) {
        List<UserCard> uCards = this.userCacheService.getUserDatas(uid, UserCard.class);
        return uCards;
    }

    /**
     * 获取玩家若干卡牌记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param cardIds
     * @return
     */
    public List<UserCard> getUserCards(long uid, List<Integer> cardIds) {
        return userCacheService.getCfgItems(uid, cardIds, UserCard.class);
    }

    /**
     * 获取玩家单张记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param cardId
     * @return
     */
    public UserCard getUserCard(long uid, int cardId) {
        return userCacheService.getCfgItem(uid, cardId, UserCard.class);
    }

    /**
     * 通过ID获取卡牌，如：传入普通水王ID  当玩家卡牌已封神为加强的水王，通过此方法将自动检测
     *
     * @param uid
     * @param cardId
     * @return
     */
    public UserCard getUserNormalCardOrDeifyCard(long uid, int cardId) {
        UserCard card = userCacheService.getCfgItem(uid, cardId, UserCard.class);
        if (card == null && cardId < 10000 && CardTool.hasDeifyInfo(cardId)) {
            //该卡可以被封神，需要检测玩家是否有封神后的卡牌
            card = userCacheService.getCfgItem(uid, cardId + 10000, UserCard.class);
        }
        return card;
    }

    /**
     * 添加玩家卡牌记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param userCard
     */
    public void addUserCard(long uid, UserCard userCard) {
        userCacheService.addUserData(userCard);
    }

    /**
     * 删除玩家卡牌记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param userCards
     */
    public void delUserCards(long uid, List<UserCard> userCards) {
        userCacheService.delUserDatas(userCards);
    }

    /**
     * 登录时获取卡牌
     *
     * @param guId
     * @return
     */
    public List<UserCard> getUserCardsAsLogin(long guId) {
        List<UserCard> uCards = getUserCards(guId);
        if (ListUtil.isEmpty(uCards)) {
            return uCards;
        }
        // 去重
        Map<Integer, List<UserCard>> cardGroups = uCards.stream().collect(Collectors.groupingBy(UserCard::getBaseId));
        Set<Integer> cardIds = cardGroups.keySet();
        List<UserCard> repeatCards = new ArrayList<>();
        for (Integer cardId : cardIds) {
            List<UserCard> ucs = cardGroups.get(cardId);
            if (ucs.size() > 1) {
                ucs.sort(Comparator.comparing(UserCard::getLevel));
                repeatCards.addAll(ucs.subList(0, ucs.size() - 1));
            }
        }
        // 重新读取
        if (ListUtil.isNotEmpty(repeatCards)) {
            for (UserCard repeatCard : repeatCards) {
                log.error("{}拥有重复的卡牌{}", guId, JSONUtil.toJson(repeatCard));
            }
        }
        //处理必要的数据迁移
        List<UserCard> cardsToUpdate = new ArrayList<>();
        for (UserCard uCard : uCards) {
            UserCard.UserCardStrengthenInfo strengthenInfo = uCard.getStrengthenInfo();
            if (null == strengthenInfo) {
                continue;
            }
            Map<String, Integer> usedSkillScrolls = strengthenInfo.getUsedSkillScrolls();
            if (null == usedSkillScrolls) {
                continue;
            }
            //从旧数据修正数据
            for (String skillLevel : usedSkillScrolls.keySet()) {
                Integer skillScrollId = usedSkillScrolls.get(skillLevel);
                if (null != skillScrollId) {
                    strengthenInfo.addUsingSkillScroll(Integer.valueOf(skillLevel), skillScrollId);
                }
            }
            strengthenInfo.setUsedSkillScrolls(null);
            cardsToUpdate.add(uCard);
        }
        if (ListUtil.isNotEmpty(cardsToUpdate)) {
            gameUserService.updateItems(cardsToUpdate);
        }
        return uCards;
    }

    /**
     * 获得编组数量上限
     *
     * @param gu
     * @return
     */
    public static int getGroupingLimit(GameUser gu) {
        return Math.min(10 + gu.getLevel() / 5, 20);
    }

    /**
     * 获取玩家城战卡组
     *
     * @param uid
     * @return
     */
    public List<UserCard> getFightingCards(long uid) {
        UserCardGroup usingCardGroup = this.userCardGroupService.getUsingGroup(uid, CardGroupWay.Normal_Fight);
        if (usingCardGroup == null) {
            return new ArrayList<>();
        }
        return getUserCards(uid, usingCardGroup.getCards());
    }

    /**
     * 获取战斗卡组
     *
     * @param uid
     * @return
     */
    public CPCardGroup getFightingCardGroup(long uid) {
        UserCardGroup usingCardGroup = this.userCardGroupService.getUsingGroup(uid, CardGroupWay.Normal_Fight);
        List<UserCard> userCards = getUserCards(uid, usingCardGroup.getCards());
        return CPCardGroup.getInstanceByUserCards(uid, usingCardGroup.getFuCe(), userCards);
    }

    /**
     * 根据卡组名称获取卡组
     *
     * @param uid
     * @param groupName
     * @return
     */
    public UserCardGroup getUserCardGroup(long uid, String groupName) {
        UserCardGroup usingCardGroup = this.userCardGroupService.getUserCardGroup(uid, groupName);
        return usingCardGroup;
    }

    /**
     * 根据卡组下标获取卡组
     *
     * @param uid
     * @param number
     * @return
     */
    public List<UserCard> getGroupCards(long uid, int groupWay, int number) {
        UserCardGroup usingCardGroup = this.userCardGroupService.getUserCardGroup(uid, groupWay, number);
        if (usingCardGroup != null) {
            return getUserCards(uid, usingCardGroup.getCards());
        }
        return new ArrayList<>();
    }

    /**
     * 给太一府、女娲庙等捐赠提供一张五星卡
     *
     * @param guId
     * @return
     */
    public int getCard5ForCityDontation(long guId) {
        long notOwnNum = getNotOwnCards(guId, 5).stream().filter(c -> c.getWay() == 0).count();
        int random = PowerRandom.getRandomBySeed(6);
        if (notOwnNum == 0 && random == 6) {
            return 236;
        }
        return CardTool.getRandomNotSpecialCard(5).getId();

    }

    /**
     * 攻城掉落卡牌
     *
     * @param guId 玩家id
     * @return
     */
    public CfgCardEntity getAttackCardAwardForCity(Long guId, int cityId) {
        GameUser gu = this.gameUserService.getGameUser(guId);

        CfgCityEntity city = CityTool.getCityById(cityId);
        String[] cardIds = city.getDropCards().split(",");

        // 处理只有三张一星卡牌的情况
        int pos = 0;
        if (cardIds.length == 3) {
            pos = PowerRandom.getRandomBySeed(3) - 1;
            return CardTool.getCardById(Integer.valueOf(cardIds[pos]));
        }

        int cityLevel = city.getLevel();
        // ------------刘少军 修改 为从 抽卡策略获取卡牌 2019-04-10
        // 城池卡牌
        RandomParam randomParams = new RandomParam();
        ArrayList<Integer> ccards = new ArrayList<Integer>();
        for (String cardId : cardIds) {
            ccards.add(Integer.parseInt(cardId));
        }
        randomParams.setCityCards(ccards);
        // 神仙
        int cardDropRate = this.godService.getCardDropRate(gu);
        // 策略
        String strategyKey = CITY_STRATEGY_KEYS[cityLevel - 1];
        Optional<CfgCardEntity> card = this.userCardRandomService.getRandomCard(gu.getId(), strategyKey, randomParams, cardDropRate);
        if (card.isPresent()) {
            return card.get();
        }
        // 不应该执行到这里
        String title = "卡牌策略[" + strategyKey + "]错误!";
        String msg = "区服sid[" + gu.getServerId() + "]玩家[" + gu.getId() + "," + gu.getRoleInfo().getNickname() + "]";
        msg += "未能从城池[" + city.getName() + "]获得卡牌！";
        CoderNotify.notifyCoderInfo(title, msg);
        // ------------ 2019-04-10 之前的原来的算法------------------------
        // 获得星级概率
        int[] array = CityConfig.bean().getCcData().getGcdl().get(cityLevel - 1);
        int rand2 = array[1], rand3 = array[2], rand4 = array[3], rand5 = array[4];
        rand3 = rand3 * (100 + cardDropRate) / 100;
        rand4 = rand4 * (100 + cardDropRate) / 100;
        rand5 = rand5 * (100 + cardDropRate) / 100;
        int random = PowerRandom.getRandomBySeed(10000);
        // 获得星级
        int star = 0;
        if (random <= rand5) {
            star = 5;
        } else if (random <= rand4 + rand5) {
            star = 4;
        } else if (random <= rand3 + rand4 + rand5) {
            star = 3;
        } else if (random <= rand2 + rand3 + rand4 + rand5) {
            star = 2;
        } else {
            star = 1;
        }

        List<CfgCardEntity> starCards = new ArrayList<CfgCardEntity>();
        for (int i = 0; i < cardIds.length; i++) {
            CfgCardEntity card1 = CardTool.getCardById(Integer.valueOf(cardIds[i]));
            if (card1.getStar() == star) {
                starCards.add(card1);
            }
        }
        random = PowerRandom.getRandomBySeed(starCards.size()) - 1;
        return starCards.get(random);
    }

    /**
     * 获得未拥有的星级卡牌，如果拥有指定四星卡牌，则随机一张
     *
     * @param guId
     * @return
     */
    public CfgCardEntity getNotOwnCard(long guId, int star, List<Integer> ways) {
        // 未拥有的途径卡牌
        List<CfgCardEntity> notOwnCards = getNotOwnCards(guId, star, ways);
        if (ListUtil.isNotEmpty(notOwnCards)) {
            return PowerRandom.getRandomFromList(notOwnCards);
        }
        return CardTool.getRandomCard(star, ways);
    }

    /**
     * 获得未拥有的星级卡牌，如果拥有指定四星卡牌，则随机一张
     *
     * @param guId
     * @return
     */
    public List<CfgCardEntity> getNotOwnCards(long guId, int star, List<Integer> ways) {
        // 未拥有的所有卡片
        List<CfgCardEntity> allNotOwnCards = getNotOwnCards(guId, star);
        // 未拥有的途径卡牌
        List<CfgCardEntity> notOwnCards = allNotOwnCards.stream().filter(card -> ways.contains(card.getWay()))
                .collect(Collectors.toList());
        return notOwnCards;
    }

    /**
     * 获得未拥有的所有卡牌
     *
     * @param guId
     * @param star
     * @return
     */
    public List<CfgCardEntity> getNotOwnCards(long guId, int star) {
        List<Integer> ownCards = getUserCards(guId).stream().map(UserCard::getBaseId)
                .collect(Collectors.toList());
        List<CfgCardEntity> allStarCards = CardTool.getAllCards(star);
        List<CfgCardEntity> nowOwnCards = allStarCards.stream().filter(card -> !ownCards.contains(card.getId()))
                .collect(Collectors.toList());
        return nowOwnCards;
    }

    /**
     * 获取玩家的展示卡信息
     *
     * @param uid
     * @return
     */
    public UserShowCard getShowCard(long uid) {
        UserShowCard showCard = this.gameUserService.getSingleItem(uid, UserShowCard.class);
        return showCard;
    }

    /**
     * 替换某张展示卡
     *
     * @param uid
     * @param oldCardId
     * @param newCardId
     */
    public void replaceShowCard(long uid, Integer oldCardId, Integer newCardId) {
        UserShowCard showCard = this.gameUserService.getSingleItem(uid, UserShowCard.class);
        if (showCard == null) {
            return;
        }
        if (ListUtil.isNotEmpty(showCard.getCardIds())) {
            if (showCard.getCardIds().contains(oldCardId)) {
                showCard.getCardIds().remove(oldCardId);
                showCard.getCardIds().add(newCardId);
                gameUserService.updateItem(showCard);
            }
        }
    }


    /**
     * 替换某些展示卡
     *
     * @param uid
     * @param oldCardIds
     * @param newCardIds
     */
    public void replaceShowCards(long uid, List<Integer> oldCardIds, List<Integer> newCardIds) {
        UserShowCard showCard = this.gameUserService.getSingleItem(uid, UserShowCard.class);
        if (showCard == null) {
            return;
        }
        if (ListUtil.isNotEmpty(showCard.getCardIds())) {
            for (Integer oldCardId : oldCardIds) {
                if (showCard.getCardIds().contains(oldCardId)) {
                    showCard.getCardIds().remove(oldCardId);
                    showCard.getCardIds().add(newCardIds.get(oldCardIds.indexOf(oldCardId)));
                }
            }
            gameUserService.updateItem(showCard);
        }
    }

    /**
     * 设置显示的卡信息
     *
     * @param uid
     * @param ids
     */
    public void setShowCard(long uid, List<Integer> ids) {
        UserShowCard showCard = this.gameUserService.getSingleItem(uid, UserShowCard.class);
        if (showCard == null) {
            showCard = UserShowCard.instance(uid);
            this.gameUserService.addItem(uid, showCard);
        }
        if (ids.size() > showCard.getShowNum()) {
            ids = ids.subList(0, showCard.getShowNum());
        }
        showCard.setCardIds(ids);
        this.gameUserService.updateItem(showCard);
    }
}
