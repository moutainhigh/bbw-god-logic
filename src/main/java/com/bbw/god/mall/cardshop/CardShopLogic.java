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
    // ????????????????????????
    public static final int UNLOCK_CARD_POOL_CONDITION = 15;

    /**
     * ???????????????
     *
     * @return
     */
    public RDCardShop getCardShopInfo(long guId) {
        RDCardShop rd = new RDCardShop();
        // ????????????
        List<RDCardPoolStatus> cardPoolStatus = getRDCardPoolStatus(guId);
        rd.setCardPoolStatus(cardPoolStatus);
        // ????????????????????????
        IActivityRank ar = activityRankService.getActivityRank(gameUserService.getActiveSid(guId), ActivityRankEnum.XIAN_YUAN_RANK);
        if (ar != null) {
            Long remainTime = ar.gainEnd().getTime() - System.currentTimeMillis();
            rd.setXybRemainTime(remainTime.intValue());
        }
        return rd;
    }

    /**
     * ????????????
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
        // ???????????????????????????????????????????????????????????????
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
                //????????????????????????
                ucPool.setWishCard(ucPool.getWishCard() + 10000);
                gameUserService.updateItem(ucPool);
            }
        }
        cardShopService.setHolidayExtraWishValue(ucPool);
        // ?????????????????????
        rd.setGoldTenDrawTimes(ucPool.getGoldTenDrawTimes());
        rd.setCurVow(ucPool.getWishValue());
        rd.setExtraVow(ucPool.getExtraWishValue());
        rd.setNeedVow(ucPool.getNeedWish());
        // ?????????
        rd.setVowCardId(ucPool.getWishCard());
        // ????????????
        List<WishCard> wishCards = cardShopService.getWishCards(guId, type);
        Optional<WishCard> newCard = wishCards.stream().filter(WishCard::isNewCard).findFirst();
        newCard.ifPresent(wishCard -> rd.setNewCardId(wishCard.getId()));
        // ????????????
        rd.setCards(getPoolCards(ucPool));
        return rd;
    }

    /**
     * ???????????????
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
     * ???????????????
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
        // ??????????????????
        /*if (ucPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.lock");
        }*/
        // ?????????????????????
        ucPool.setWishCard(cardId);
        ucPool.setNeedWish(wishCard.getNeedWish());
        ucPool.setWishValue(0);
        cardShopService.setHolidayExtraWishValue(ucPool);
        gameUserService.updateItem(ucPool);
        return new RDSuccess();
    }

    /**
     * ????????????
     *
     * @param type
     * @return
     */
    public RDCardShop activeCardPool(long guId, int type) {
        CardPoolEnum typeEnum = CardPoolEnum.fromValue(type);
        if (typeEnum == null) {
            throw new ExceptionForClientTip("cardpool.not.valid");
        }
        // ?????????????????????
        if (typeEnum == CardPoolEnum.GOLD_CP) {
            throw new ExceptionForClientTip("cardpool.not.need.unlock");
        }
        // ???????????????????????????
        CardPoolEnum prePoolType = CardShopTool.getPreCardPool(typeEnum);
        UserCardPool preCardPool = cardShopService.getCardPoolRecords(guId, prePoolType.getValue());
        if (preCardPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.need.unlock.preone");
        }
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        // ??????????????????
        if (ucPool.getIsUnlock() == CardPoolStatusEnum.UNLOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.already.unlock");
        }
        // ?????????????????????
        TreasureEnum needTreasureType = CardShopTool.getNeedYJAsActive(typeEnum);
        TreasureChecker.checkIsEnough(needTreasureType.getValue(), 1, guId);
        TreasureEventPublisher.pubTDeductEvent(guId, needTreasureType.getValue(), 1, WayEnum.UNLOCK_CARD_POOL, new RDCommon());
        // ????????????
        ucPool.setIsUnlock(1);
        gameUserService.updateItem(ucPool);

        // ????????????????????????
        RDCardShop rd = new RDCardShop();
        List<RDCardPoolStatus> cardPoolStatus = getRDCardPoolStatus(guId);
        rd.setCardPoolStatus(cardPoolStatus);
        return rd;
    }

    /**
     * ??????
     *
     * @param guId
     * @param type      ????????????
     * @param drawTimes ????????????
     * @return
     */
    public RDCardDraw draw(long guId, int type, int drawTimes) {
        // ??????????????????
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
     * ??????????????????
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
            //?????????ID????????????????????????ID
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
        // ?????????????????????
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
            // ????????????????????????
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
        // ???????????????
        if (!isValidActivity) {
            cardPoolStatus = cardPoolStatus.stream().filter(tmp ->
                    tmp.getCardPool() != CardPoolEnum.LIMIT_TIME_CP.getValue()).collect(Collectors.toList());
        }
        return cardPoolStatus;
    }
}
