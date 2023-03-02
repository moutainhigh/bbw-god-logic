package com.bbw.god.game.chanjie;

import com.bbw.exception.ExceptionForClientTip;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lwb
 * @date 2019年6月14日
 * @version 1.0 说明：阐截斗法 Url接口检测，当阐截斗法设置关闭时，所有方法禁止访问
 */
@Component
@Aspect
public class ChanjieAspect {
	@Autowired
	private ChanjieStatusService chanjieStatusService;

	@Pointcut("execution(public * com.bbw.god.game.chanjie..ChanjieCtrl.*(..))")
	public void executeService() {
	}

	@Around("executeService()")
	public Object doAroundAdvice(ProceedingJoinPoint point) {
		if (!chanjieStatusService.isOpen()) {
			throw new ExceptionForClientTip("chanjie.not.open");
		}
		try {
			Object obj = point.proceed(); // 执行方法
			return obj;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return null;
	}
}
