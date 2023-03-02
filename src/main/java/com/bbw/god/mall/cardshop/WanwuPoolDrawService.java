package com.bbw.god.mall.cardshop;

import com.bbw.god.ConsumeType;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @author suhq
 * @date 2020-04-20 16:56
 **/
@Service
public class WanwuPoolDrawService extends AbstractDrawService {
    public static final String COMMON_STRATEGY = "卡池_万物_单抽";
    public static final String WISH_STRATEGY = "卡池_万物_单抽_许愿";
    public static final String GUARANTEE_STRATEGY = "卡池_万物_10连抽补偿";

    /**
     * 万物卡池
     *
     * @param cardPool
     * @param costType
     * @param num
     * @param rd
     */
    @Override
    CardDrawResult drawPool(UserCardPool cardPool, ConsumeType costType, int num, RDCardDraw rd) {
        WayEnum way = getWay(CardPoolEnum.WANWU_CP);
        long guId = cardPool.getGameUserId();
        //包括当月在内，接下来3个月内，即将产出的签到卡，玩家将不会在卡池中抽到
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
        CardEventPublisher.pubCardAddEvent(guId, cardAddInfos, way, "开万物卡池", rd);
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


    @Override
    List<CfgCardEntity> drawByStrategy(UserCardPool cardPool, int num, RDCardDraw rd) {
        boolean has4Star = false;
        List<CfgCardEntity> cardIds = new ArrayList<>(num);
        String strategyKey = WISH_STRATEGY;
        long guId = cardPool.getGameUserId();
        int addedVow = 0;
        List<UserCard> ownCards = userCardService.getUserCards(guId);
        Optional<CfgCardEntity> matchCard = null;
        for (int i = 0; i < num; i++) {

            // 每次都需要判定是否有许愿卡（许愿卡中途获得后会重置）
            if (cardPool.getWishCard() == -1) {
                strategyKey = COMMON_STRATEGY;
            }
            // 十连抽保底四星
            if (i == 9 && !has4Star) {
                strategyKey = GUARANTEE_STRATEGY;
            }
            // 加许愿值
            int addedWishValue = getAddedWishValue();

            //处理伪随机的失败次数
            int failTimes = getFailTimes(guId, cardPool, strategyKey, addedWishValue);
            // 策略抽卡
            RandomParam randomParams = getRandomParamForDraw(cardPool, ownCards);
            matchCard = userCardRandomService.getRandomCardWithFailTimes(guId, strategyKey, randomParams, failTimes);
            // 如果没有获得策略结果，且是许愿策略，则根据一般策略获取结果
            if (!matchCard.isPresent() && strategyKey.equals(WISH_STRATEGY)) {
                matchCard = userCardRandomService.getRandomCard(guId, COMMON_STRATEGY, randomParams);
            }
            if (matchCard.isPresent()) {
                int matchCardId = matchCard.get().getId();
                cardIds.add(matchCard.get());
                if (matchCard.get().getStar() >= 4) {
                    has4Star = true;
                }

                if (cardPool.ifHasWishCard()) {
                    // 如果抽到许愿卡则重置许愿卡
                    if (matchCardId == cardPool.getWishCard()) {
                        cardPool.setWishCard(-1);
                        cardPool.setNeedWish(0);
                        cardPool.setWishValue(0);
                        cardPool.setExtraWishValue(0);
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
    public RandomParam getRandomParamForDraw(UserCardPool cardPool, List<UserCard> ownCards) {
        RandomParam randomParams = super.getRandomParamForDraw(cardPool, ownCards);
        int wishCardId = cardPool.getWishCard();
        if (wishCardId > 0) {
            CfgCardEntity card = CardTool.getCardById(wishCardId);
            randomParams.set("$许愿卡", Arrays.asList(card.getName()));
            randomParams.set("$保底值", String.valueOf(cardPool.getNeedWish()));
            randomParams.set("$概率", String.valueOf(getProbabilityForWishCard(cardPool)));
        }
        return randomParams;
    }

    @Override
    WayEnum getWay(CardPoolEnum type) {
        return WayEnum.OPEN_WANWU_CARD_POOL;
    }

    /**
     * 获得许愿卡概率
     *
     * @param cardPool
     * @return
     */
    private double getProbabilityForWishCard(UserCardPool cardPool) {
        double progress = (cardPool.getWishValue() + cardPool.getExtraWishValue()) * 1.00 / cardPool.getNeedWish();
        Integer wishCard = cardPool.getWishCard();
        Integer star = CardTool.getCardById(wishCard).getStar();
        if (progress <= 0.2) return 5 == star ? 0.01 : 0.1;
        if (progress <= 0.4) return 5 == star ? 0.05 : 0.5;
        if (progress <= 0.6) return 5 == star ? 0.1 : 1;
        if (progress <= 0.8) return 5 == star ? 0.2 : 2;
        if (progress <= 0.99) return 5 == star ? 0.3 : 6;
        return 100;
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
        UserCardRandom ucr = null;
        if (!uRnd.isPresent() || (uRnd.isPresent() && uRnd.get().getSelectorLoopTimes() == null)) {
            failTimes += cardPool.getWishValue();
        }
        return failTimes;
    }
}
