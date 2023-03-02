package com.bbw.god.city.yed;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author suchaobin
 * @description 野地丢骰子切片
 * @date 2020/6/2 16:47
 **/
@Slf4j
@Aspect
@Component
public class YeDShakeAspect {

	@Around("execution(* com.bbw.god.gameuser.shake.ShakeService.getDiceResult(..))")
	@Order(2)
	public Object getDiceResult(ProceedingJoinPoint point) throws Throwable {
		Object[] args = point.getArgs();
		GameUser gu = (GameUser) args[0];
		Integer diceNum = (Integer) args[1];
		long uid = gu.getId();
		List<Integer> shakeList = new ArrayList<>();
		RDYeDEventCache cache = TimeLimitCacheUtil.getYeDEventCache(uid);
		Set<Integer> eventIds = cache.getEventIds();
		if (eventIds.contains(YdEventEnum.ZJZF.getValue())) {
			for (int i = 0; i < diceNum; i++) {
				shakeList.add(6);
			}
			eventIds.remove(YdEventEnum.ZJZF.getValue());
			TimeLimitCacheUtil.setYeDEventCache(uid, cache);
			return shakeList;
		} else if (eventIds.contains(YdEventEnum.CBNX.getValue())) {
			shakeList.add(1);
			eventIds.remove(YdEventEnum.CBNX.getValue());
			TimeLimitCacheUtil.setYeDEventCache(uid, cache);
			return shakeList;
		}
		return point.proceed();
	}
}
