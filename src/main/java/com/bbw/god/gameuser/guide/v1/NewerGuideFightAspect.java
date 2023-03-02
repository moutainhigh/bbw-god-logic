package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yed.RDYeDEventCache;
import com.bbw.god.city.yeg.IYegFightProcessor;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.city.yeg.YeGFightProcessorFactory;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.exaward.YeGExawardEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.rd.RDAdvance;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author suhq
 * @description: 新手引导摇骰子切片
 * @date 2019-12-26 14:41
 **/
@Slf4j
@Aspect
@Component
public class NewerGuideFightAspect {
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private YeGFightProcessorFactory yeGFightProcessorFactory;

    @Around("execution(* com.bbw.god.city.yeg.YeGProcessor.arriveProcessor(..))")
    public Object arriveYeGProcessor(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GameUser gu = (GameUser) args[0];
        CfgCityEntity city = (CfgCityEntity) args[1];
        RDAdvance rd = (RDAdvance) args[2];
        if (!this.newerGuideService.isPassNewerGuide(gu.getId())) {
            long uid = gu.getId();
            IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(YeGuaiEnum.YG_NORMAL);
            RDYeDEventCache yeDEventCache = TimeLimitCacheUtil.getYeDEventCache(uid);
            Set<Integer> eventIds = yeDEventCache.getEventIds();
            if (eventIds.contains(YdEventEnum.JIN_WEI_JUN.getValue())) {
                fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(YeGuaiEnum.YG_ELITE);
                eventIds.remove(YdEventEnum.JIN_WEI_JUN.getValue());
                TimeLimitCacheUtil.setYeDEventCache(uid, yeDEventCache);
            }
            RDFightsInfo rdFightsInfo = fightProcessor.getFightsInfo(gu, city.getType() - 100);
            // 初始战斗为未结算
            TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
            RDArriveYeG rdArriveYeG = RDArriveYeG.getInstance(rdFightsInfo, city,
                    YeGExawardEnum.WIN_6_ROUND.getVal());
            rdArriveYeG.setYeGuaiType(fightProcessor.getYeGEnum().getType());
            return rdArriveYeG;
        }
        return point.proceed();
    }
}
