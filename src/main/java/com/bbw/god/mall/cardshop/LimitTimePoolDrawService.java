package com.bbw.god.mall.cardshop;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandom;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author suchaobin
 * @description 限时卡池抽卡
 * @date 2021/2/3 15:29
 **/
@Service
public class LimitTimePoolDrawService extends AbstractDrawService {
    @Autowired
    private ActivityService activityService;

    public static final String COMMON_STRATEGY = "卡池_限时_单抽";
    public static final String GUARANTEE_STRATEGY = "卡池_限时_10连抽补偿";


    /**
     * 抽卡
     *
     * @param cardPool
     * @param costType
     * @param num
     * @param rd
     * @return
     */
    @Override
    CardDrawResult drawPool(UserCardPool cardPool, ConsumeType costType, int num, RDCardDraw rd) {
        long guId = cardPool.getGameUserId();
        int sid = gameUserService.getActiveSid(guId);
        //是否有效活动
        boolean isValidActivity = isValidActivity(sid);
        // 活动未生效
        if (!isValidActivity) {
            throw new ExceptionForClientTip("cardpool.lock");
        }
        WayEnum way = WayEnum.OPEN_LIMIT_TIME_CARD_POOL;
        List<CfgCardEntity> cards = drawByStrategy(cardPool, num, rd);
        List<Integer> cardIds = new ArrayList<>();
        CfgCardEntity maxStarCard = cards.get(0);// 最高星级卡牌
        List<EVTreasure> treasures = new ArrayList<>();// 灵石
        for (CfgCardEntity card : cards) {
            if (RandomKeys.isPowerStarCard(card.getId())) {
                // 灵石卡
                treasures.add(new EVTreasure(card.getId(), 1));
            } else {
                // 普通卡
                cardIds.add(card.getId());
            }
            if (card.getStar() > maxStarCard.getStar()) {
                maxStarCard = card;
            }
        }
        CardDrawResult cardDrawResult = new CardDrawResult(num, way);
        // 发放卡牌
        List<EPCardAdd.CardAddInfo> cardAddInfos = CardEventPublisher.getCardAddInfos(guId, cardIds);
        cardDrawResult.setAddCards(cardAddInfos);
        CardEventPublisher.pubCardAddEvent(guId, cardAddInfos, way, "开限时卡池", rd);
        // 发放灵石
        if (treasures.size() > 0) {
            TreasureEventPublisher.pubTAddEvent(guId, treasures, way, rd);
            cardDrawResult.setTreasures(treasures);
        }
        // 加元宝10连次数
        if (costType == ConsumeType.XZY && num == 10) {
            cardPool.addGoldTenDrawTimes();
        }
        gameUserService.updateItem(cardPool);
        return cardDrawResult;
    }

    /**
     * 是否有效活动
     *
     * @param sid
     * @return
     */
    public boolean isValidActivity(int sid) {
        IActivity a = activityService.getActivity(sid, ActivityEnum.LIMIT_TIME_CARD_POOL);
        if (null != a && a.ifTimeValid()) {
            return true;
        }
        a = activityService.getActivity(sid, ActivityEnum.LIMIT_TIME_CARD_POOL_51);
        if (null != a && a.ifTimeValid()) {
            return true;
        }
        return false;
    }

    /**
     * 策略抽卡
     *
     * @param cardPool
     * @param num
     * @param rd
     * @return
     */
    @Override
    List<CfgCardEntity> drawByStrategy(UserCardPool cardPool, int num, RDCardDraw rd) {
        boolean has4Star = false;
        List<CfgCardEntity> cardIds = new ArrayList<>(num);
        String strategyKey = COMMON_STRATEGY;
        long guId = cardPool.getGameUserId();
        int addedVow = 0;
        List<UserCard> ownCards = userCardService.getUserCards(guId);
        Optional<CfgCardEntity> matchCardResult = null;
        for (int i = 0; i < num; i++) {
            // 十连抽保底四星
            if (i == 9 && !has4Star) {
                strategyKey = GUARANTEE_STRATEGY;
            }
            // 策略抽卡
            RandomParam randomParams = getRandomParamForDraw(cardPool, ownCards);
            matchCardResult = userCardRandomService.getRandomCard(guId, strategyKey, randomParams);

            // 加许愿值
            int addedWishValue = getAddedWishValue();

            if (matchCardResult.isPresent()) {
                CfgCardEntity matchCard = matchCardResult.get();
                //五星保底卡
                CfgCardEntity wishCard = CardTool.getCardById(cardPool.getWishCard());
                int failTimes = cardPool.getWishValue() + cardPool.getExtraWishValue() + addedWishValue;
                if (failTimes >= cardPool.getNeedWish()) {
                    matchCard = wishCard;
                }
                int matchCardId = matchCard.getId();
                cardIds.add(matchCard);
                //标记高星卡
                if (matchCard.getStar() >= 4) {
                    has4Star = true;
                }

                if (cardPool.ifHasWishCard()) {
                    if (matchCard.isCanDeify()) {
                        matchCardId = Integer.parseInt("10" + matchCardId);
                    }
                    // 如果抽到许愿卡则重置许愿卡
                    if (matchCardId == cardPool.getWishCard() || matchCardId % 1000 == cardPool.getWishCard()) {
                        cardPool.setWishValue(0);
                        cardShopService.setHolidayExtraWishValue(cardPool);
                    } else {
                        cardPool.addWishValue(addedWishValue);
                        addedVow += addedWishValue;
                    }
                }

            } else {
                thowExceptionAsNotExistStrategy(guId, strategyKey, cardPool.getCardPool());
            }
        }
        rd.setAddedVow(addedVow);
        return cardIds;
    }

    @Override
    WayEnum getWay(CardPoolEnum type) {
        return WayEnum.OPEN_LIMIT_TIME_CARD_POOL;
    }

    /**
     * 获取失败次数（相对策略保底机制）
     *
     * @param uid
     * @param cardPool
     * @param strategyKey
     * @param addedValue
     * @return
     */
    protected int getFailTimes(long uid, UserCardPool cardPool, String strategyKey, int addedValue) {
        int failTimes = addedValue;
        Optional<UserCardRandom> uRnd = userCardRandomService.getUserStrategy(uid, strategyKey);
        if (!uRnd.isPresent() || (uRnd.isPresent() && uRnd.get().getSelectorLoopTimes() == null)) {
            failTimes += cardPool.getWishValue();
        }
        return failTimes;
    }
}
