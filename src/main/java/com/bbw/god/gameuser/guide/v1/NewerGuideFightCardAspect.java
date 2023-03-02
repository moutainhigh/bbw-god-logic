package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.rd.RDCommon;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author suhq
 * @description: 新手引导战斗卡牌掉落切片
 * @date 2019-12-26 14:41
 **/
@Aspect
@Component
public class NewerGuideFightCardAspect {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewerGuideService newerGuideService;

    @Around("execution(* com.bbw.god.gameuser.card.UserCardService.getAttackCardAwardForCity(..))")
    public Object getAttackCardAwardForCity(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Long uid = (Long) args[0];
        GameUser gu = this.gameUserService.getGameUser(uid);
        int newerGuide = this.newerGuideService.getNewerGuide(uid);
        NewerGuideEnum guide = NewerGuideEnum.fromValue(newerGuide);
        if (guide == NewerGuideEnum.ATTACK) {
            // 新手引导攻城后送卡牌：方弼
            return CardTool.getCardById(517);
        }
        return point.proceed();
    }

    @Around("execution(* com.bbw.god.city.yeg.YeGProcessor.openBox(..))")
    public Object openBox(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Long uid = (Long) args[0];
        int newerGuide = this.newerGuideService.getNewerGuide(uid);
        NewerGuideEnum guide = NewerGuideEnum.fromValue(newerGuide);
        if (guide == NewerGuideEnum.YEGUAI) {
            RDFightEndInfo fightEndInfo = TimeLimitCacheUtil.getFightEndCache(uid);
            int freeTime = null == fightEndInfo.getFreeTime() ? 0 : fightEndInfo.getFreeTime();
            if (freeTime > 0) {
                return openBoxForNewerGuide(uid);
            }
        }
        return point.proceed();
    }

    private RDCommon openBoxForNewerGuide(long guId) {
        RDFightEndInfo fightEndInfo = TimeLimitCacheUtil.getFightEndCache(guId);
        int freeTime = fightEndInfo.getFreeTime();
        int remainTime = fightEndInfo.getRemainTime();
        RDCommon rdCommon = new RDCommon();
        if (freeTime == 2) {
            // 新手引导打完野怪后，打开第一个箱子送卡牌：花翎鸟
            CardEventPublisher.pubCardAddEvent(guId, 115, WayEnum.YG_OPEN_BOX, "打野开箱子获得", rdCommon);
        }
        if (freeTime == 1) {
            // 新手引导打完野怪后，打开第二个箱子送卡牌：方相
            CardEventPublisher.pubCardAddEvent(guId, 518, WayEnum.YG_OPEN_BOX, "打野开箱子获得", rdCommon);
        }
        freeTime--;
        remainTime--;
        fightEndInfo.setFreeTime(freeTime);
        fightEndInfo.setRemainTime(remainTime);
        TimeLimitCacheUtil.setFightEndCache(guId, fightEndInfo);
        rdCommon.setFreeTimes(freeTime);
        return rdCommon;
    }
}
