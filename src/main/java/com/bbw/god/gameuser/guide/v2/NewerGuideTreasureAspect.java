package com.bbw.god.gameuser.guide.v2;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author suchaobin
 * @description 新手引导 法宝切片
 * @date 2019/12/30 10:28
 */
//@Aspect
//@Component
public class NewerGuideTreasureAspect {
	@Autowired
	private NewerGuideService newerGuideService;

	@Around("execution(* com.bbw.god.gameuser.treasure.processor.DingFZProcessor.getNeedNum(..))")
	public Object getAttackCardAwardForCity(ProceedingJoinPoint point) throws Throwable {
		Object[] args = point.getArgs();
		GameUser gu = (GameUser) args[0];
		if (!newerGuideService.isPassNewerGuide(gu.getId())) {
			return 1;
		}
		return point.proceed();
	}
}
