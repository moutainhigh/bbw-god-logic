package com.bbw.god.mall.cardshop;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgWishCard;
import com.bbw.god.game.config.CfgWishCard.WishCard;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardShopService {
    public static List<CardPoolEnum> cardPoolSerials = Arrays.asList(CardPoolEnum.GOLD_CP, CardPoolEnum.WATER_CP, CardPoolEnum.WOOD_CP, CardPoolEnum.FIRE_CP, CardPoolEnum.EARTH_CP, CardPoolEnum.WANWU_CP);
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 初始化卡池记录
     *
     * @param guId
     */
    public void initUserCardPool(long guId) {
        List<UserCardPool> ucPools = getCardPoolRecords(guId);
        if (ListUtil.isEmpty(ucPools)) {
            Date now = DateUtil.now();
            for (int i = 0; i < cardPoolSerials.size(); i++) {
                CardPoolEnum cpEnum = cardPoolSerials.get(i);
                Date awardEndDate = DateUtil.addDays(now, i);
                List<WishCard> wishCards = getWishCards(guId, cpEnum.getValue());
                UserCardPool ucp = null;
                switch (cpEnum) {
                    case WANWU_CP:
                    case JUX_CP:
                        ucp = UserCardPool.Instance(guId, cpEnum.getValue(), CardPoolStatusEnum.UNLOCK, awardEndDate);
                        break;
                    default:
                        ucp = UserCardPool.Instance(guId, cpEnum.getValue(), CardPoolStatusEnum.LOCK, awardEndDate, wishCards.get(0));
                        break;
                }
                setHolidayExtraWishValue(ucp);
                ucPools.add(ucp);
            }
            gameUserService.addItems(ucPools);
        }
        initIfNotExist(guId, null, ucPools, CardPoolEnum.JUX_CP, CardPoolStatusEnum.UNLOCK);
        initIfNotExist(guId, getWishCards(guId, CardPoolEnum.LIMIT_TIME_CP.getValue()).get(0),
                ucPools, CardPoolEnum.LIMIT_TIME_CP, CardPoolStatusEnum.UNLOCK);
    }

    private void initIfNotExist(long guId, WishCard wishCard, List<UserCardPool> ucPools, CardPoolEnum poolEnum, CardPoolStatusEnum statusEnum) {
        // 是否存在聚贤卡池
        boolean exist = ucPools.stream().anyMatch(tmp -> tmp.getCardPool().equals(poolEnum.getValue()));
        // 不存在则初始化
        if (!exist) {
            UserCardPool ucp = UserCardPool.Instance(guId, poolEnum.getValue(), statusEnum);
            if (null != wishCard) {
                ucp.setWishCard(wishCard.getId());
                ucp.setNeedWish(wishCard.getNeedWish());
            }
            gameUserService.addItem(guId, ucp);
        }
    }

    public void setHolidayExtraWishValue(UserCardPool ucp) {
        int sid = gameUserService.getActiveSid(ucp.getGameUserId());
        IActivity a = this.activityService.getGameActivity(sid, ActivityEnum.HOLIDAY_WISH_FEEDBACK);
        // 活动期间内设置节日额外进度
        if (null != a) {
            Integer needWish = ucp.getNeedWish();
            int extraWishValue = (int) (0.2 * needWish);
            if (ucp.getWishValue() + extraWishValue > needWish) {
                extraWishValue = needWish - ucp.getWishValue();
            }
            ucp.setExtraWishValue(extraWishValue);
            gameUserService.updateItem(ucp);
        }
        // 活动结束后重置节日额外进度
        if (null == a && ucp.getExtraWishValue() > 0) {
            List<Integer> wishCards = getWishCards(ucp.getGameUserId(), CardPoolEnum.WANWU_CP.getValue()).stream()
                    .map(WishCard::getId).collect(Collectors.toList());
            // 当前万物卡池的许愿卡不在许愿池中，则清除
            if (CardPoolEnum.WANWU_CP.getValue() == ucp.getCardPool() && !wishCards.contains(ucp.getWishCard())) {
                ucp.setWishCard(-1);
                ucp.setWishValue(0);
                ucp.setNeedWish(0);
            }
            ucp.setExtraWishValue(0);
            gameUserService.updateItem(ucp);
        }
    }

    /**
     * 重置卡池：此方法将重置玩家卡池的解锁状态，解锁时间倒计时
     *
     * @param guId
     */
    public void resetUserCardPool(long guId) {
        List<UserCardPool> ucPools = getCardPoolRecords(guId);
        Date now = DateUtil.now();
        List<CardPoolEnum> enums = CardShopTool.getCardPoolEnum();
        if (!ListUtil.isEmpty(ucPools)) {
            for (UserCardPool cardPool : ucPools) {
                // 如果是万物卡池或者聚贤卡池的，直接解锁
                List<Integer> list = Arrays.asList(CardPoolEnum.WANWU_CP.getValue(), CardPoolEnum.JUX_CP.getValue());
                boolean isLock = true;
                // 其他卡池要判断卡牌数量
                if (!list.contains(cardPool.getCardPool())) {
                    List<UserCard> userCards = userCardService.getUserCards(guId);
                    long count = userCards.stream().filter(tmp ->
                            CardTool.getCardById(tmp.getBaseId()).getType().equals(cardPool.getCardPool())).count();
                    isLock = count >= CardShopLogic.UNLOCK_CARD_POOL_CONDITION;
                }
                int index = enums.indexOf(CardPoolEnum.fromValue(cardPool.getCardPool()));
                Date awardEndDate = DateUtil.addDays(now, index);
                cardPool.resetLock(isLock, awardEndDate);
            }
            gameUserService.updateItems(ucPools);
        }
    }

    public UserCardPool getCardPoolRecords(long guId, int type) {
        List<UserCardPool> ucPools = gameUserService.getMultiItems(guId, UserCardPool.class);
        UserCardPool ucPool = ucPools.stream().filter(tmp -> tmp.getCardPool() == type).findFirst().get();
        return ucPool;
    }

    public List<UserCardPool> getCardPoolRecords(long guId) {
        List<UserCardPool> ucPools = gameUserService.getMultiItems(guId, UserCardPool.class);
        // 如果卡池数据生成多批了，需要删除多余的数据
        if (ListUtil.isNotEmpty(ucPools) && ucPools.size() > CardPoolEnum.values().length) {
            // 卡池类型与卡池数据的映射
            Map<Integer, List<UserCardPool>> map = ucPools.stream().collect(Collectors.groupingBy(UserCardPool::getCardPool));
            List<UserCardPool> toDel = new ArrayList<>();
            for (Integer cardPool : map.keySet()) {
                List<UserCardPool> cardPools = map.get(cardPool);
                // 预备要删除的数据
                if (cardPools.size() > 1) {
                    toDel.addAll(cardPools.subList(1, cardPools.size()));
                }
            }
            if (ListUtil.isNotEmpty(toDel)) {
                gameUserService.deleteItems(guId, toDel);
                ucPools = gameUserService.getMultiItems(guId, UserCardPool.class);
            }
        }
        return ucPools;
    }

    public List<WishCard> getWishCards(long uid, int type) {
        CfgWishCard config = Cfg.I.getUniqueConfig(CfgWishCard.class);
        List<WishCard> wishCards = config.getWishCards().get(type / 10 - 1);
        // 避免多次添加和活动结束后无法正常消失
        List<WishCard> wishCardList = new ArrayList<>(wishCards);
        IActivity a = this.activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.HOLIDAY_WISH_FEEDBACK);
        if (null != a && CardPoolEnum.WANWU_CP.getValue() == type) {
            WishCard wishCard = new WishCard();
            wishCard.setId(328);
            wishCard.setName("玉面银狐");
            wishCard.setNeedWish(1000);
            wishCardList.add(0, wishCard);
        }
        return wishCardList;
    }

    /**
     * 清空限时卡池许愿值
     */
    public void resetCardPoolWish(long uid){
        //获得玩家限时卡池
        List<UserCardPool> ucPools = getCardPoolRecords(uid);
        UserCardPool limitTimeCardPool = ucPools.stream().filter(tmp -> tmp.getCardPool() == CardPoolEnum.LIMIT_TIME_CP.getValue()).findFirst().orElse(null);
        //卡池为空
        if (null == limitTimeCardPool) {
            return;
        }
        limitTimeCardPool.resetWishValue();
        gameUserService.updateItems(ucPools);
    }

}
