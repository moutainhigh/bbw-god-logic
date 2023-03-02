package com.bbw.god.mall.cardshop;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.game.config.CfgWishCard.WishCard;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.repairdata.ResetCardShopService;
import com.bbw.god.mall.cardshop.RDCardShop.RDCardPoolStatus;
import com.bbw.god.mall.cardshop.RDWishCardPool.RDWishCard;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardShopLogic {

    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private CardShopService cardShopService;

    @Autowired
    private TypePoolDrawService typePoolDrawService;
    @Autowired
    private WanwuPoolDrawService wanwuPoolDrawService;
    @Autowired
    private JuXPoolDrawService juXPoolDrawService;
    @Autowired
    private LimitTimePoolDrawService limitTimePoolDrawService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ResetCardShopService resetCardShopService;
    // 解锁属性卡池限制
    public static final int UNLOCK_CARD_POOL_CONDITION = 15;

    /**
     * 卡牌屋信息
     *
     * @return
     */
    public RDCardShop getCardShopInfo(long guId) {
        RDCardShop rd = new RDCardShop();
        // 卡池状态
        List<RDCardPoolStatus> cardPoolStatus = getRDCardPoolStatus(guId);
        rd.setCardPoolStatus(cardPoolStatus);
        // 返回仙缘榜倒计时
        IActivityRank ar = activityRankService.getActivityRank(gameUserService.getActiveSid(guId), ActivityRankEnum.XIAN_YUAN_RANK);
        if (ar != null) {
            Long remainTime = ar.gainEnd().getTime() - System.currentTimeMillis();
            rd.setXybRemainTime(remainTime.intValue());
        }
        return rd;
    }

    /**
     * 卡池信息
     *
     * @param type
     * @return
     */
    public RDCardPool getCardPoolInfo(long guId, int type) {
        RDCardPool rd = new RDCardPool();
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        if (CardPoolEnum.LIMIT_TIME_CP.getValue() == type && 561 != ucPool.getWishCard().intValue()) {
            resetCardShopService.resetLimitCardsPool(guId);
        }
        // 如果属性卡池被意外重置，则重新初始化许愿卡
        if (ucPool.getWishCard() == -1 || ucPool.getNeedWish() == 0) {
            List<WishCard> wishCards = cardShopService.getWishCards(guId, ucPool.getCardPool());
            if (ListUtil.isNotEmpty(wishCards) && wishCards.size() == 1) {
                ucPool.setWishCard(wishCards.get(0).getId());
                ucPool.setNeedWish(wishCards.get(0).getNeedWish());
                gameUserService.updateItem(ucPool);
            }
        }
        if (ucPool.getWishCard() > 0 && ucPool.getWishCard() < 10000) {
            CfgCardEntity cardEntity = CardTool.getCardById(ucPool.getWishCard());
            if (cardEntity.isCanDeify() && userCardService.getUserCard(guId, ucPool.getWishCard() + 10000) != null) {
                //已经获得封神卡牌
                ucPool.setWishCard(ucPool.getWishCard() + 10000);
                gameUserService.updateItem(ucPool);
            }
        }
        cardShopService.setHolidayExtraWishValue(ucPool);
        // 元宝十连抽次数
        rd.setGoldTenDrawTimes(ucPool.getGoldTenDrawTimes());
        rd.setCurVow(ucPool.getWishValue());
        rd.setExtraVow(ucPool.getExtraWishValue());
        rd.setNeedVow(ucPool.getNeedWish());
        // 许愿卡
        rd.setVowCardId(ucPool.getWishCard());
        // 处理新卡
        List<WishCard> wishCards = cardShopService.getWishCards(guId, type);
        Optional<WishCard> newCard = wishCards.stream().filter(WishCard::isNewCard).findFirst();
        newCard.ifPresent(wishCard -> rd.setNewCardId(wishCard.getId()));
        // 卡牌预览
        rd.setCards(getPoolCards(ucPool));
        return rd;
    }

    /**
     * 许愿池信息
     *
     * @return
     */
    public RDWishCardPool getWishPoolInfo(long uid) {
        RDWishCardPool rd = new RDWishCardPool();
        int type = CardPoolEnum.WANWU_CP.getValue();
        List<WishCard> wishCards = cardShopService.getWishCards(uid, type);
        List<RDWishCard> wishCardIds = wishCards.stream()
                .map(tmp -> {
                    IActivity a = this.activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.HOLIDAY_WISH_FEEDBACK);
                    int extraVow = null == a ? 0 : (int) (tmp.getNeedWish() * 0.2);
                    return new RDWishCard(tmp.getId(), extraVow, tmp.getNeedWish(), tmp.isNewCard());
                })
                .collect(Collectors.toList());
        rd.setCards(wishCardIds);
        return rd;
    }

    /**
     * 添加到卡池
     *
     * @param cardId
     * @return
     */
    public RDSuccess addToPool(long guId, int cardId) {
        int type = CardPoolEnum.WANWU_CP.getValue();
        List<WishCard> wishCards = cardShopService.getWishCards(guId, type);
        Optional<WishCard> wishCardOptional = wishCards.stream().filter(tmp -> tmp.getId() == cardId).findFirst();
        if (!wishCardOptional.isPresent()) {
            throw new ExceptionForClientTip("cardpool.not.this.card");
        }
        WishCard wishCard = wishCardOptional.get();
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        // 卡池是否解锁
        /*if (ucPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.lock");
        }*/
        // 处理新的许愿卡
        ucPool.setWishCard(cardId);
        ucPool.setNeedWish(wishCard.getNeedWish());
        ucPool.setWishValue(0);
        cardShopService.setHolidayExtraWishValue(ucPool);
        gameUserService.updateItem(ucPool);
        return new RDSuccess();
    }

    /**
     * 激活卡池
     *
     * @param type
     * @return
     */
    public RDCardShop activeCardPool(long guId, int type) {
        CardPoolEnum typeEnum = CardPoolEnum.fromValue(type);
        if (typeEnum == null) {
            throw new ExceptionForClientTip("cardpool.not.valid");
        }
        // 金卡池无需解锁
        if (typeEnum == CardPoolEnum.GOLD_CP) {
            throw new ExceptionForClientTip("cardpool.not.need.unlock");
        }
        // 前一个卡池是否解锁
        CardPoolEnum prePoolType = CardShopTool.getPreCardPool(typeEnum);
        UserCardPool preCardPool = cardShopService.getCardPoolRecords(guId, prePoolType.getValue());
        if (preCardPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.need.unlock.preone");
        }
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        // 卡池是否解锁
        if (ucPool.getIsUnlock() == CardPoolStatusEnum.UNLOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.already.unlock");
        }
        // 晶石检查并扣除
        TreasureEnum needTreasureType = CardShopTool.getNeedYJAsActive(typeEnum);
        TreasureChecker.checkIsEnough(needTreasureType.getValue(), 1, guId);
        TreasureEventPublisher.pubTDeductEvent(guId, needTreasureType.getValue(), 1, WayEnum.UNLOCK_CARD_POOL, new RDCommon());
        // 标记解锁
        ucPool.setIsUnlock(1);
        gameUserService.updateItem(ucPool);

        // 获取所有卡池状态
        RDCardShop rd = new RDCardShop();
        List<RDCardPoolStatus> cardPoolStatus = getRDCardPoolStatus(guId);
        rd.setCardPoolStatus(cardPoolStatus);
        return rd;
    }

    /**
     * 抽卡
     *
     * @param guId
     * @param type      卡池类型
     * @param drawTimes 抽卡次数
     * @return
     */
    public RDCardDraw draw(long guId, int type, int drawTimes) {
        // 卡池是否存在
        CardPoolEnum typeEnum = CardPoolEnum.fromValue(type);
        if (typeEnum == null) {
            throw new ExceptionForClientTip("cardpool.not.valid");
        }
        if (type == CardPoolEnum.WANWU_CP.getValue()) {
            return wanwuPoolDrawService.draw(guId, type, drawTimes);
        }
        if (type == CardPoolEnum.JUX_CP.getValue()) {
            return juXPoolDrawService.draw(guId, type, drawTimes);
        }
        if (type == CardPoolEnum.LIMIT_TIME_CP.getValue()) {
            return limitTimePoolDrawService.draw(guId, type, drawTimes);
        }
        return typePoolDrawService.draw(guId, type, drawTimes);
    }

    /**
     * 获得卡池预览
     *
     * @param ucPool
     * @return
     */
    private List<Integer> getPoolCards(UserCardPool ucPool) {
        CardPoolEnum cpEnum = CardPoolEnum.fromValue(ucPool.getCardPool());
        long uid = ucPool.getGameUserId();
        List<UserCard> userCards = userCardService.getUserCards(uid);
        String strategyKey = WanwuPoolDrawService.COMMON_STRATEGY;
        RandomParam param = typePoolDrawService.getRandomParamForDraw(ucPool, userCards);
        switch (cpEnum) {
            case WANWU_CP:
                break;
            case JUX_CP:
                strategyKey = JuXPoolDrawService.JU_XIAN_CARD_POOL_STRATEGY_7;
                param = juXPoolDrawService.getRandomParamForDraw(ucPool, userCards);
                break;
            case LIMIT_TIME_CP:
                strategyKey = LimitTimePoolDrawService.COMMON_STRATEGY;
                param = limitTimePoolDrawService.getRandomParamForDraw(ucPool, userCards);
                break;
            default:
                strategyKey = TypePoolDrawService.COMMON_STRATEGIES.get(ucPool.getCardPool() / 10 - 1);
                param = wanwuPoolDrawService.getRandomParamForDraw(ucPool, userCards);
                break;
        }
        List<Integer> cardIds = new ArrayList<>();
        if (ucPool.getWishCard() > 0) {
            cardIds.add(ucPool.getWishCard());
        }
        List<CfgCardEntity> cards = RandomCardService.getStrategyCards(strategyKey, param);
        List<Integer> deifyCards = new ArrayList<>();
        for (CfgCardEntity card : cards) {
            if (card.isCanDeify()) {
                Optional<UserCard> optional = userCards.stream().filter(p -> p.getBaseId() == (card.getId() + 10000)).findFirst();
                optional.ifPresent(userCard -> deifyCards.add(userCard.getBaseId()));
            }
        }
        List<Integer> finalCardIds = cardIds;
        List<Integer> strategyCards = cards.stream().filter(tmp -> !finalCardIds.contains(tmp.getId()) && !finalCardIds.contains(tmp.getId() + 10000) && tmp.getId() < 600).map(CfgCardEntity::getId)
                .collect(Collectors.toList());
        if (ListUtil.isNotEmpty(deifyCards)) {
            //将旧卡ID替换成封神的新卡ID
            for (int id : deifyCards) {
                int index = strategyCards.indexOf(id % 10000);
                if (index > -1) {
                    strategyCards.remove(index);
                    strategyCards.add(index, id);
                }
            }
        }
        cardIds.addAll(strategyCards);
        int serverGroupId = gameUserService.getActiveGid(ucPool.getGameUserId());
        // 小游戏临时处理
        if (80 == serverGroupId) {
            List<Integer> excludeId = Arrays.asList(262, 302, 360, 463, 10302);
            cardIds = cardIds.stream().filter(tmp -> !excludeId.contains(tmp)).collect(Collectors.toList());
        }
        return cardIds;
    }

    private List<RDCardPoolStatus> getRDCardPoolStatus(Long guId) {
        List<UserCardPool> ucPools = cardShopService.getCardPoolRecords(guId);
        List<RDCardPoolStatus> cardPoolStatus = new ArrayList<RDCardShop.RDCardPoolStatus>();
        for (UserCardPool pool : ucPools) {
            RDCardPoolStatus ps = RDCardPoolStatus.instance(pool.getCardPool(), pool.getIsUnlock());
            // 晶石奖励剩余时间
            CardPoolEnum prevPoolEnum = CardShopTool.getPreCardPool(CardPoolEnum.fromValue(pool.getCardPool()));
            Long yjRemainTime = -1L;
            if (prevPoolEnum != null && !CardPoolEnum.WANWU_CP.equals(prevPoolEnum)) {
                Optional<UserCardPool> prePoolOp = ucPools.stream().filter(p -> p.getCardPool() == prevPoolEnum.getValue()).findFirst();
                if (prePoolOp.isPresent()) {
                    yjRemainTime = prePoolOp.get().getAwardEndDate().getTime() - System.currentTimeMillis();
                }
            }
            ps.setYjRemainTime(yjRemainTime.intValue());
            cardPoolStatus.add(ps);
        }

        int sid = gameUserService.getActiveSid(guId);
        boolean isValidActivity = limitTimePoolDrawService.isValidActivity(sid);
        // 活动未生效
        if (!isValidActivity) {
            cardPoolStatus = cardPoolStatus.stream().filter(tmp ->
                    tmp.getCardPool() != CardPoolEnum.LIMIT_TIME_CP.getValue()).collect(Collectors.toList());
        }
        return cardPoolStatus;
    }
}
