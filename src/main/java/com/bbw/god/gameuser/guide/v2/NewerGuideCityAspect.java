package com.bbw.god.gameuser.guide.v2;

import com.bbw.god.city.chengc.in.RDBuildingOutputs;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 新手引导，城池切片
 * @date 2019/12/30 10:21
 */
//@Aspect
//@Component
//TODO 待优化
public class NewerGuideCityAspect {
	@Autowired
	private NewerGuideService newerGuideService;

	/**
	 * 新手引导，炼宝炉固定出 混元金斗
	 *
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.bbw.god.city.chengc.in.building.LianBL.doBuildingAward(..))")
	public Object doBuildingAward(ProceedingJoinPoint point) throws Throwable {
		Object[] args = point.getArgs();
		GameUser gu = (GameUser) args[0];
		RDBuildingOutputs.RDBuildingOutput rd = (RDBuildingOutputs.RDBuildingOutput) args[2];
		if (!newerGuideService.isPassNewerGuide(gu.getId())) {
			TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.HYJD.getValue(), 1, WayEnum.LBL_AWARD, rd);
			return null;
		}
		return point.proceed();
	}
}
