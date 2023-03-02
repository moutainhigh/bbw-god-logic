package com.bbw.god.mall.cardshop;

import com.bbw.god.ConsumeType;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.random.service.RandomParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author suhq
 * @date 2020-04-20 16:56
 **/
@Service
public class TypePoolDrawService extends AbstractDrawService {
    public static final List<String> COMMON_STRATEGIES = Arrays.asList("卡池_单抽_金", "卡池_单抽_木", "卡池_单抽_水", "卡池_单抽_火", "卡池_单抽_土");
    public static final String GUARANTEE_STRATEGY = "卡池_属性_10连抽补偿";

    /**
     * 属性卡池
     *
     * @param cardPool
     * @param costType
     * @param num
     * @param rd
     */
    @Override
    CardDrawResult drawPool(UserCardPool cardPool, ConsumeType costType, int num, RDCardDraw rd) {
        CardPoolEnum cardPoolEnum = CardPoolEnum.fromValue(cardPool.getCardPool());
        WayEnum way = getWay(cardPoolEnum);
        long guId = cardPool.getGameUserId();
        List<CfgCardEntity> cards = drawByStrategy(cardPool, num, rd);
        List<Integer> cardIds = cards.stream().mapToInt(CfgCardEntity::getId).boxed().collect(Collectors.toList());

        CardDrawResult cardDrawResult = new CardDrawResult(num, way);
        // 发放卡牌
        List<EPCardAdd.CardAddInfo> cardAddInfos = CardEventPublisher.getCardAddInfos(guId, cardIds);
        cardDrawResult.setAddCards(cardAddInfos);
        CardEventPublisher.pubCardAddEvent(guId, cardAddInfos, way, "开" + cardPoolEnum.getName(), rd);

        // 加元宝10连次数
        if (costType == ConsumeType.XZY && num == 10) {
            cardPool.addGoldTenDrawTimes();
            // 指定时间内元宝10连抽送源晶
            /*Long yjRemainTime = cardPool.getAwardEndDate().getTime() - System.currentTimeMillis();
            if (yjRemainTime > 0 && cardPool.getGoldTenDrawTimes() == 1) {
                TreasureEnum awardYJType = CardShopTool.getAwardYJ(cardPoolEnum);
                TreasureEventPublisher.pubTAddEvent(guId, awardYJType.getValue(), 1, way, rd);
                cardDrawResult.addEvTreasure(awardYJType.getValue(), 1);
            }*/
        }
        gameUserService.updateItem(cardPool);
        return cardDrawResult;
    }


    @Override
    List<CfgCardEntity> drawByStrategy(UserCardPool cardPool, int num, RDCardDraw rd) {
        boolean has4Star = false;
        List<CfgCardEntity> cardIds = new ArrayList<>(num);
        String strategyKey = COMMON_STRATEGIES.get(cardPool.getCardPool() / 10 - 1);
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

    /**
     * 获取策略参数
     *
     * @param cardPool
     * @param ownCards
     * @return
     */
    @Override
    public RandomParam getRandomParamForDraw(UserCardPool cardPool, List<UserCard> ownCards) {
        RandomParam randomParams = super.getRandomParamForDraw(cardPool, ownCards);
        randomParams.set("$卡包属性", String.valueOf(cardPool.getCardPool()));
        return randomParams;
    }

    @Override
    WayEnum getWay(CardPoolEnum type) {
        switch (type) {
            case GOLD_CP:
                return WayEnum.OPEN_GOLD_CARD_POOL;
            case WOOD_CP:
                return WayEnum.OPEN_WOOD_CARD_POOL;
            case WATER_CP:
                return WayEnum.OPEN_WATER_CARD_POOL;
            case FIRE_CP:
                return WayEnum.OPEN_FIRE_CARD_POOL;
            case EARTH_CP:
                return WayEnum.OPEN_EARTH_CARD_POOL;
            case WANWU_CP:
                return WayEnum.OPEN_WANWU_CARD_POOL;
            case JUX_CP:
                return WayEnum.OPEN_JU_XIAN_CARD_POOL;
            case LIMIT_TIME_CP:
                return WayEnum.OPEN_LIMIT_TIME_CARD_POOL;
        }
        return null;
    }
}
