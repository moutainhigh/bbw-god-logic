package com.bbw.sys.log;

import com.alibaba.fastjson.JSON;
import com.bbw.common.IpUtil;
import com.bbw.god.login.LoginPlayer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 系统日志，切面处理类
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-06 15:08
 */
@Aspect
@Component
public class SysLogAspect {
	@Autowired
	private SysLogService sysLogService;

	@Pointcut("@annotation(com.bbw.sys.log.SysLog)")
	public void logPointCut() {

	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;

		//保存日志
		saveSysLog(point, time);

		return result;
	}

	private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		SysLogEntity sysLog = new SysLogEntity();
		SysLog syslog = method.getAnnotation(SysLog.class);
		if (syslog != null) {
			//注解上的描述
			sysLog.setOperation(syslog.value());
		}

		//请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.setMethod(className + "." + methodName + "()");

		//请求的参数
		Object[] args = joinPoint.getArgs();
		try {
			String params = toJson(args[0]);
			sysLog.setParams(params);
		} catch (Exception e) {

		}

		//获取request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		//设置IP地址
		sysLog.setIp(IpUtil.getIpAddr(request));

		//用户名
		try {
			LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
			if (null != player) {
				sysLog.setUsername(player.getUid().toString());
			} else {
				sysLog.setUsername("未登录");
			}
		} catch (Exception e) {
			sysLog.setUsername("获取失败");
		}
		sysLog.setTime(time);
		sysLog.setCreateDate(new Date());
		//保存系统日志
		sysLogService.insert(sysLog);
	}

	private String toJson(Object object) {
		if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Boolean || object instanceof String) {
			return String.valueOf(object);
		}
		return JSON.toJSONString(object);
	}
}
