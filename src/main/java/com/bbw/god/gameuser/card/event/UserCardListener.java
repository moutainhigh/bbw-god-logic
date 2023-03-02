package com.bbw.god.gameuser.card.event;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.ShareCacheUtil;
import com.bbw.god.cache.ShareCacheUtil.ShareStatus;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.card.event.EPCardAdd.CardAddInfo;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.card.CardResStatisticService;
import com.bbw.god.gameuser.task.timelimit.UserCardVigor;
import com.bbw.god.mall.cardshop.*;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDCardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserCardListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private CardResStatisticService statisticService;
    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;
    @Autowired
    private CardShopService cardShopService;
    @Autowired
    private PrivilegeService privilegeService;


    /**
     * 卡牌编组
     *
     * @param event
     */
    @EventListener
    public void grouping(UserCardGroupingEvent event) {
        EPCardGrouping ep = event.getEP();
        String[] values = ep.getCardGroups().split("!");
        List<Integer> cardIds = CardParamParser.parseGroupParam(values[0]);
        long uid = ep.getGuId();
        int deck = Integer.valueOf(values[1]);
        //判断玩家是否拥有地灵印
        if (deck == UserCardGroupService.CARD_GROUP_6 && !privilegeService.isOwnDiLing(uid)) {
            return;
        }
        //判断玩家是否有天灵印
        if (deck == UserCardGroupService.CARD_GROUP_7 && !privilegeService.isOwnTianLing(uid)) {
            return;
        }
        UserCardGroup userCardGroup = userCardGroupService.getUserCardGroup(ep.getGuId(), CardGroupWay.Normal_Fight.getValue(), deck);
        if (userCardGroup != null) {
            userCardGroup.setCards(cardIds);
            gameUserService.updateItem(userCardGroup);
        }
    }

    /**
     * 卡牌获得经验
     *
     * @param event
     */
    @EventListener
    public void addExp(UserCardExpAddEvent event) {
        EPCardExpAdd ep = event.getEP();
        long uid = ep.getGuId();
        RDCommon rd = ep.getRd();
        int cardId = ep.getCardId();
        int addExp = ep.getAddedExp();
        //如果拥有天灵印，获得的经验多百分之十
        if (privilegeService.isOwnTianLing(uid)) {
            addExp *= 1.1;
        }
        UserCard userCard = userCardService.getUserCard(uid, cardId);
        CfgCardEntity card = userCard.gainCard();
        userCard.addExp(addExp);
        gameUserService.updateItem(userCard);
        // 处理等级
        int preLevel = userCard.getLevel();
        int newLevel = CardExpTool.getUpdatedLevel(card, userCard.getExperience(), preLevel);
        if (newLevel > preLevel) {
            BaseEventParam bep = new BaseEventParam(ep.getGuId(), ep.getWay(), ep.getRd());
            EPCardLevelUp epCardLevelUp = new EPCardLevelUp(bep, cardId, preLevel, newLevel);
            CardEventPublisher.pubCardLevelUpEvent(epCardLevelUp);
        }
        rd.setCardExp(userCard.getExperience() - CardExpTool.getExpByLevel(card, newLevel));
        rd.setCard(cardId);
    }

    /**
     * 卡牌升阶
     */
    @EventListener
    @Order(1)
    public void hierarchyUp(UserCardHierarchyUpEvent event) {
        EPCardHierarchyUp ep = event.getEP();
        int cardId = ep.getCardId();
        UserCard userCard = userCardService.getUserCard(ep.getGuId(), cardId);
        userCard.addHierarchy();
        if (userCard.getHierarchy() % 2 == 0) {
            UserCardVigor cardVigor = gameUserService.getSingleItem(ep.getGuId(), UserCardVigor.class);
            if (null != cardVigor) {
                boolean needToUpdate = cardVigor.addCardVigor(userCard, 1);
                if (needToUpdate) {
                    gameUserService.updateItem(cardVigor);
                }
            }
        }
        gameUserService.updateItem(userCard);
    }

    /**
     * 卡牌升级
     *
     * @param event
     */
    @EventListener
    public void levelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        RDCommon rd = ep.getRd();
        int cardId = ep.getCardId();
        int newLevel = ep.getNewLevel();
        UserCard userCard = userCardService.getUserCard(ep.getGuId(), cardId);
        userCard.setLevel(newLevel);
        gameUserService.updateItem(userCard);
        rd.setCardLevel(newLevel);

    }

    /**
     * 获得卡牌
     *
     * @param event
     */
    @EventListener
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
        List<CardAddInfo> cardAddInfos = ep.getAddCards();
        RDCommon rd = ep.getRd();
        cardAddInfos.stream().forEach(epCard -> addCard(gameUser, epCard, ep.getWay(), ep.getBroadcastWayInfo(), rd));
    }

    @EventListener
    public void delCard(UserCardDelEvent event) {
        EPCardDel ep = event.getEP();
        List<UserCard> uCards = ep.getDelCards();
        if (ListUtil.isNotEmpty(uCards)) {
            uCards.forEach(tmp -> log.info(tmp.toString()));
            userCardService.delUserCards(ep.getGuId(), uCards);
            String key = statisticService.getKey(ep.getGuId(), StatisticTypeEnum.GAIN);
            redisHashUtil.delete(key);
            statisticService.init(ep.getGuId());
        }
    }

    /**
     * 扣除卡牌灵石
     *
     * @param event
     */
    @EventListener
    public void delCardLingShi(UserCardLingshiDeductEvent event) {
        EPCardLingShi ep = event.getEP();
        RDCommon rdCommon = ep.getRd();
        List<UserCard> list = new ArrayList<>();
        List<RDCommon.RDCardLingshi> lingshis = new ArrayList<>();
        for (EPCardLingShi.LingShiInfo info : ep.getLingShiInfos()) {
            UserCard userCard = userCardService.getUserCard(ep.getGuId(), info.getCardId());
            if (userCard == null) {
                //卡牌不存在
                throw new ExceptionForClientTip("card.not.exists");
            }
            if (userCard.getLingshi() < info.getNum()) {
                //灵石不够扣
                throw new ExceptionForClientTip("card.lingshi.not.enough", userCard.getName());
            }
            userCard.addLingshi(-Math.abs(info.getNum()));
            list.add(userCard);
            lingshis.add(RDCommon.RDCardLingshi.instance(userCard.getBaseId(), -Math.abs(info.getNum())));
        }
        gameUserService.updateItems(list);
        rdCommon.setDeductedCardLingshi(lingshis);
    }

    /**
     * 添加卡牌
     *
     * @param gu
     * @param epCard
     * @param way
     * @param broadcastWayInfo
     * @param rd
     */
    private void addCard(GameUser gu, CardAddInfo epCard, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        RDCardInfo rdCardInfo = new RDCardInfo();
        int cardId = epCard.getCardId();
        UserCard uCard = userCardService.getUserNormalCardOrDeifyCard(gu.getId(), cardId);
        if (uCard != null) {
            //封神后的卡此处卡ID将转换成封神卡ID
            cardId = uCard.getBaseId();
        }
        rdCardInfo.setCard(cardId);
        CfgCardEntity cfgCard = CardTool.getCardById(cardId);
        if (epCard.isNew() && uCard == null) {
            uCard = UserCard.fromCfgCard(gu.getId(), cfgCard, way);
            userCardService.addUserCard(gu.getId(), uCard);
            rdCardInfo.setDataId(uCard.getId());
            // 设置分享缓存
            if (cfgCard.getStar() > 3) {
                ShareCacheUtil.setShareableCard(gu.getId(), cardId, ShareStatus.ENABLE_AWARD);
            }
        } else {
            epCard.setNew(false);
            epCard.setCardId(uCard.getBaseId());
            // 已有卡牌，转为灵石
            uCard.addLingshi(cfgCard.getStar());
            gameUserService.updateItem(uCard);
            rdCardInfo.setAddSoul(cfgCard.getSoulId());
            rdCardInfo.setSoulNum(cfgCard.getStar());
        }
        rd.addCard(rdCardInfo);
    }

    @Async
    @EventListener
    public void unlockCardPool(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        Long uid = ep.getGuId();
        List<UserCardPool> userCardPools = cardShopService.getCardPoolRecords(uid)
                .stream().filter(tmp -> !tmp.ifUnlock()).collect(Collectors.toList());
        // 不存在未解锁卡池，直接return
        if (ListUtil.isEmpty(userCardPools)) {
            return;
        }
        // 需要更新的卡池数据
        List<UserCardPool> updateCardPools = new ArrayList<>();
        // 提前获取玩家所有卡牌数据
        List<UserCard> userCards = userCardService.getUserCards(uid);
        for (UserCardPool pool : userCardPools) {
            Integer type = pool.getCardPool();
            // 同属性卡牌数量是否有20张
            long count = userCards.stream().filter(tmp -> CardTool.getCardById(tmp.getBaseId()).getType().equals(type)).count();
            // 达到可解锁条件
            if (count >= CardShopLogic.UNLOCK_CARD_POOL_CONDITION || type == CardPoolEnum.WANWU_CP.getValue()) {
                pool.setIsUnlock(CardPoolStatusEnum.UNLOCK.getValue());
                updateCardPools.add(pool);
            }
        }
        if (ListUtil.isNotEmpty(updateCardPools)) {
            gameUserService.updateItems(updateCardPools);
        }
    }

    /**
     * 点将台抽到已获得的卡牌，判断是否暴击
     *
     * @return
     */
    private boolean isCrit() {
        return PowerRandom.getRandomBySeed(100) <= 10;
    }
}
