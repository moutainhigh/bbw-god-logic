package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.cunz.RDArriveChunZ;
import com.bbw.god.city.kez.RDArriveKeZ;
import com.bbw.god.city.xianrd.RDArriveXianRD;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.StarEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 新手引导切片
 * @date 2019-12-26 14:41
 **/
@Aspect
@Component
public class NewerGuideArriveAspect {
    private static final int[] KZ_THIRD_CARDS = {111, 214, 313, 410, 514};
    @Autowired
    private NewerGuideService newerGuideService;

    @Around("execution(* com.bbw.god.city.*.*.arriveProcessor(..))")
    public Object handleArriveGuide(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GameUser gu = (GameUser) args[0];
        CfgCityEntity city = (CfgCityEntity) args[1];
        if (null==args[2]){
            return point.proceed();
        }
        RDAdvance rd = (RDAdvance) args[2];
        NewerGuideEnum guide = NewerGuideEnum.fromValue(this.newerGuideService.getNewerGuide(gu.getId()));
        switch (guide) {
            case START:
                return arriveCunz(gu, city, rd);
            case CUNZHUANG:
                return arriveXianRD(gu, city, rd);
            case XIANRENDONG:
                return arriveKeZ(gu);
            case KZ_BUY:
                return arriveYeG(gu, city, rd);
            default:
                return point.proceed();
        }
    }

    private RDArriveChunZ arriveCunz(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        // 一张同属性元素
        ResEventPublisher.pubEleAddEvent(gu.getId(), gu.getRoleInfo().getCountry(), 1, WayEnum.CZ, rd);
        // 一张同属性一星卡牌
        CfgCardEntity card = CardTool.getRandomNotSpecialCard(gu.getRoleInfo().getCountry(), StarEnum.One.getValue());
        CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), WayEnum.CZ, "在村庄获得", rd);
        return RDArriveChunZ.fromRDCommon(rd);
    }

    private RDArriveXianRD arriveXianRD(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        // 新手引导,赠送一个七香车
        TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.QXC.getValue(), 1, WayEnum.XRD, rd);
        return RDArriveXianRD.fromRDCommon(rd);
    }

    private RDArriveKeZ arriveKeZ(GameUser gu) {
        List<CfgCardEntity> cards = CardTool.getRandomNotSpecialCards(0, 0, 0, 2);
        cards.add(2, CardTool.getCardById(KZ_THIRD_CARDS[gu.getRoleInfo().getCountry() / 10 - 1]));
        return RDArriveKeZ.getInstance(cards);
    }

    public RDArriveYeG arriveYeG(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        RDFightsInfo rdFightsInfo = generateYGCardsAsGuide(gameUser);
        // 初始战斗为未结算
        TimeLimitCacheUtil.removeCache(gameUser.getId(), RDFightResult.class);

        return RDArriveYeG.getInstance(rdFightsInfo, city, 2);
    }

    /**
     * 生成新手引导野怪卡牌
     *
     * @param gu
     * @return
     */
    private RDFightsInfo generateYGCardsAsGuide(GameUser gu) {
        int ygLevel = 1, cardNum = 2;
        List<CfgCardEntity> cards = CardTool.getRandomNotSpecialCards(1, cardNum);
        List<UserCard> opponentCards = cards.stream().map(card -> UserCard.instance(card.getId(), 0, 0))
                .collect(Collectors.toList());
        return new RDFightsInfo(ygLevel, opponentCards);
    }
}
