package com.bbw.god.controller;

import com.bbw.App;
import com.bbw.cache.GameUserCacheAspect;
import com.bbw.common.HttpRequestUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.login.LoginCtrl;
import com.bbw.god.login.LoginPlayer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 用于打印请求返回数据
 *
 * @author suhq
 * @date 2019年3月19日 下午4:01:28
 */
//@Profile({"dev-suhq","dev-fzj"})
@Slf4j(topic = "requestAspect")
@Aspect
@Component
public class RequestAspect {
    private static final long MAX_MILLIS = 500L;
    private static List<String> INCLUDE = Arrays.asList("GameUserController.shakeDice", "LoginCtrl.login");
    @Autowired
    private GameUserCacheAspect gameUserCacheService;
    @Autowired
    private App app;

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private Object printResponObj(ProceedingJoinPoint point) throws Throwable {
        long begin = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
        Object returnObj = null;
        try {
            returnObj = point.proceed();
        } finally {
            if (null != player) {
                gameUserCacheService.deductSeed(player.getUid());
            }
        }
        log(point, returnObj, begin);
        return returnObj;
    }

    private void log(ProceedingJoinPoint jp, Object returnObj, long beginTime) {
        long endTime = System.currentTimeMillis();
        long usedTime = endTime - beginTime;
        try {
            //获取类的字节码对象，通过字节码对象获取方法信息
            Class<?> targetCls = jp.getTarget().getClass();
            //获取方法签名(通过此签名获取目标方法信息)
            MethodSignature ms = (MethodSignature) jp.getSignature();
            //获取目标方法上的注解指定的操作名称
//            Method targetMethod = targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
            //获取目标方法名(目标类型+方法名)
            String targetClsName = targetCls.getName();
            String method = targetClsName + "." + ms.getName();
            if (!isToLog(method)) {
                return;
            }
            //获取请求参数
//            String targetMethodParams = Arrays.toString(jp.getArgs());
            //请求及响应结果
            StringBuilder reqRespInfo = new StringBuilder();
            reqRespInfo.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            if (jp.getTarget() instanceof AbstractController) {
                AbstractController ctrl = (AbstractController) jp.getTarget();
                Object uid = ctrl.getUserId();
                if (null != uid) {
                    reqRespInfo.append("\n登录用户uid: ").append(uid);
                }
                reqRespInfo.append("\n请求地址:");
                reqRespInfo.append("\n\t").append(HttpRequestUtil.getLogString(ctrl.request));
            } else if (jp.getTarget() instanceof LoginCtrl) {
                LoginCtrl loginCtrl = (LoginCtrl) jp.getTarget();
                reqRespInfo.append("\n请求地址:");
                reqRespInfo.append("\n\t").append(HttpRequestUtil.getLogString(loginCtrl.request));
            }
            reqRespInfo.append("\n处理方法: ").append(method);
//            reqRespInfo.append("\n参数列表: ").append(targetMethodParams);
            reqRespInfo.append("\n返回结果:");
            reqRespInfo.append("\n\t").append(JSONUtil.toJson(returnObj));
            reqRespInfo.append("\n耗时(ms): ").append(usedTime);
            reqRespInfo.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            log.info(reqRespInfo.toString());
            if (usedTime > MAX_MILLIS) {
                log.error(reqRespInfo.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isToLog(String method) {
        if (!app.runAsProd()) {
            return true;
        }
        boolean isMatch = INCLUDE.stream().anyMatch(tmp -> method.contains(tmp));
        return isMatch;
    }

}