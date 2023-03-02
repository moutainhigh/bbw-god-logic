package com.bbw.god.city.kez;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.AbstractSpecialCityProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidaySpecialCityFactory;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.city.kez.RDArriveKeZ.RDCardPrice;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 客栈 - 提供随机3张卡牌
 *
 * @author suhq
 * @date 2018年10月24日 下午5:51:33
 */
@Component
public class KeZProcessor implements ICityArriveProcessor, ICityHandleProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.KZ);
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private HolidaySpecialCityFactory holidaySpecialBulidFactory;
    @Override
    public List<CityTypeEnum> getCityTypes() {
        return this.cityTypes;
    }

    @Override
    public Class<RDArriveKeZ> getRDArriveClass() {
        return RDArriveKeZ.class;
    }

    @Override
    public RDArriveKeZ arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        activityEvent(gu.getId(), rd);

        List<CfgCardEntity> cards = getCards(gu);
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_1.getId())){
            //必定含有一张5星卡
            List<UserCard> userCards=userCardService.getUserCards(gu.getId());
            Optional<CfgCardEntity> optional = cards.stream().filter(p -> p.getStar() == 5).findFirst();
            if (!optional.isPresent()){
                List<CfgCardEntity> randomCards = CardTool.getRandomCardsWithKez(userCards, 5, 1);
                if (!cards.isEmpty()){
                    cards.set(0,randomCards.get(0));
                    PowerRandom.shuffle(cards);
                }
            }
            BaseEventParam bep=new BaseEventParam(gu.getId(),WayEnum.KZ);
            HexagramEventPublisher.pubHexagramBuffDeductEvent(bep,HexagramBuffEnum.HEXAGRAM_1.getId(),1);
        }else if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_57.getId())){
            //所有卡牌为5星，且只能用2个捆仙绳购买
            BaseEventParam bep=new BaseEventParam(gu.getId(),WayEnum.KZ);
            HexagramEventPublisher.pubHexagramBuffDeductEvent(bep,HexagramBuffEnum.HEXAGRAM_57.getId(),1);
            List<UserCard> userCards=userCardService.getUserCards(gu.getId());
            long count = cards.stream().filter(p -> p.getStar() == 5).count();
            if (count<3){
                List<CfgCardEntity> randomCards = CardTool.getRandomCardsWithKez(userCards, 5, 3);
                if (randomCards.size()==3){
                    cards=randomCards;
                }
            }
            RDArriveKeZ rdArriveKeZ = RDArriveKeZ.getInstance(cards);
            for (RDCardPrice card : rdArriveKeZ.getCards()) {
                card.setPrice(0);
                card.setMustKxs(2);
            }
            return rdArriveKeZ;
        }
        return RDArriveKeZ.getInstance(cards);

    }

    /**
     * 活动事件
     *
     * @param uid
     * @param rd
     */
    private void activityEvent(long uid, RDAdvance rd) {
        AbstractSpecialCityProcessor specialBuildProcessor = holidaySpecialBulidFactory.getSpecialCityProcessor(uid);
        if (null == specialBuildProcessor) {
            return;
        }
        specialBuildProcessor.keZTriggerEvent(uid, rd);
    }

    /**
     * 购买卡牌
     */
    @Override
    public RDCommon handleProcessor(GameUser gu, Object param) {
        RDCommon rd = new RDCommon();
        String[] paramInts = ((String) param).split(",");
        int cardId = Integer.valueOf(paramInts[0]);
        int treasureId = Integer.valueOf(paramInts[1]);

        List<RDCardPrice> availableCards = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass()).getCards();
        // 客栈是有卡牌
        if (availableCards == null) {
            throw new ExceptionForClientTip("city.kz.not.exist");
        }
        RDCardPrice cardPrice = availableCards.stream().filter(card -> card.getId() == cardId).findFirst().orElse(null);
        // 客户端传的卡牌是否有效
        if (cardPrice == null) {
            throw new ExceptionForClientTip("city.kz.not.exist");
        }
        int needTreasureNum=1;
        if (cardPrice.getMustKxs()>0){
            //必须要用捆仙绳
            needTreasureNum=cardPrice.getMustKxs();
            if (treasureId == 0){
                throw new ExceptionForClientTip("kz.card.must.kxs",cardPrice.getMustKxs());
            }
        }
        if (treasureId != 0) {
            // 使用捆仙绳购买卡牌
            TreasureChecker.checkIsEnough(TreasureEnum.KXS.getValue(),needTreasureNum,gu.getId());
            TreasureEventPublisher.pubTDeductEvent(gu.getId(), treasureId, needTreasureNum, WayEnum.KZ, rd);
        } else {
            // 使用铜钱购买卡牌
            ResChecker.checkCopper(gu, cardPrice.getPrice());
            ResEventPublisher.pubCopperDeductEvent(gu.getId(), cardPrice.getPrice().longValue(), WayEnum.KZ, rd);
        }

        CardEventPublisher.pubCardAddEvent(gu.getId(), cardId, WayEnum.KZ, "客栈购买", rd);

        return rd;
    }

    /**
     * 客栈卡牌
     * @param gu 玩家对象
     * @return
     */
    private List<CfgCardEntity> getCards(GameUser gu) {
        RandomStrategy strategy = RandomCardService.getSetting(RandomKeys.KE_ZHAN_NORMAL);
        RandomParam randomParam = new RandomParam();
        List<UserCard> userCards=userCardService.getUserCards(gu.getId());
        randomParam.setExtraCardsToMap(userCards);
        RandomResult result = RandomCardService.getRandomList(strategy, randomParam);
        return result.getCardList();
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.kz.already.buy";
    }

}
