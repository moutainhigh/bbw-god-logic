package com.bbw.god.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.ID;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.InsAccessLogEntity;
import com.bbw.god.db.entity.InsErrorLogEntity;
import com.bbw.god.detail.async.AccessDetailAsyncHandler;
import com.bbw.god.detail.async.ErrorDetailAsyncHandler;
import com.bbw.god.login.LoginPlayer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月25日 上午8:57:37 类说明
 */
//@Profile({"dev-suhq"})
@Aspect
@Component
@Slf4j
public class GodRequestAspect {
    @Autowired
    private AccessDetailAsyncHandler accessDetailAsyncHandler;
    @Autowired
    private ErrorDetailAsyncHandler errorDetailAsyncHandler;

    /**
     * 监听所有控制层 god包下所有的控制层，目前控制层的命名都为 *Ctrl 或 *Controller 记录 res非0 的访问
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.bbw.god..*Ctrl.*(..)) || execution(* com.bbw.god..*Controller.*(..))")
    public Object allRequest(ProceedingJoinPoint point) throws Throwable {
        try {
            Object result = point.proceed();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            dolog(request, result);
            return result;
        } catch (Exception e) {
            if (!(e instanceof ExceptionForClientTip)) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                doErrorlog(request, e.getClass().toString());
                log.error(e.getMessage(), e);
            }
            throw e;
        }
    }

    /**
     * 生成log实体类，并发布保存
     *
     * @param request
     * @param result
     */
    private void dolog(HttpServletRequest request, Object result) {
        String respParam = postHandle(result);
        dolog(request, respParam);
    }

    /**
     * 记录log
     *
     * @param request
     * @param respParam 请求返回Json字符串数据 JSON.toJSONString()
     */
    private void dolog(HttpServletRequest request, String respParam) {
        if (StrUtil.isBlank(respParam)) {
            // 没有返回参数 不记录
            return;
        }
        String ip = getRemoteHost(request);
        String method = request.getRequestURI().toString();
        if (method.indexOf("/gm/") > -1) {
            return;
        }
        if (method.indexOf("/godLogic/") > -1) {
            method = method.substring(10);
        }
        String protocol = request.getProtocol();
        String params = preHandle(request);
        InsAccessLogEntity logEntity = new InsAccessLogEntity();
        logEntity.setId(ID.getNextDetailId());
        logEntity.setIp(ip);
        logEntity.setMethod(method);
        logEntity.setParams(params);
        logEntity.setProtocol(protocol);
        logEntity.setRes(respParam);
        logEntity.setType(request.getMethod());
        Optional<LoginPlayer> optional = getUser(request);
        if (optional.isPresent()) {
            Long uid = optional.get().getUid();
            uid = uid == null ? 0 : uid;
            logEntity.setUid(uid);
        }
        JSONObject jObject = JSONObject.parseObject(respParam);
        if (!jObject.containsKey("res") || !jObject.getString("res").equals("0")) {
            // res非0的日志全部保存
            accessDetailAsyncHandler.log(logEntity);
        }
    }

    private void doErrorlog(HttpServletRequest request, String respParam) {
        if (StrUtil.isBlank(respParam)) {
            // 没有返回参数 不记录
            respParam = "错误异常，未捕捉到有用信息";
        }
        String ip = getRemoteHost(request);
        String method = request.getRequestURI().toString();
        if (method.indexOf("/godLogic/") > -1) {
            method = method.substring(10);
        }
        String protocol = request.getProtocol();
        String params = preHandle(request);
        InsErrorLogEntity logEntity = new InsErrorLogEntity();
        logEntity.setId(ID.getNextDetailId());
        logEntity.setIp(ip);
        logEntity.setMethod(method);
        logEntity.setParams(params);
        logEntity.setProtocol(protocol);
        logEntity.setRes(respParam);
        logEntity.setType(request.getMethod());
        Optional<LoginPlayer> optional = getUser(request);
        if (optional.isPresent()) {
            Long uid = optional.get().getUid();
            uid = uid == null ? 0 : uid;
            logEntity.setUid(uid);
        }
        errorDetailAsyncHandler.log(logEntity);
    }

    /**
     * 获取登录对象
     *
     * @return
     */
    private static Optional<LoginPlayer> getUser(HttpServletRequest request) {
        LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
        return Optional.ofNullable(player);
    }

    /**
     * 入参数据
     *
     * @param request
     * @return
     */
    private static String preHandle(HttpServletRequest request) {
        String reqParam = "";
        Enumeration<String> paramter = request.getParameterNames();
        while (paramter.hasMoreElements()) {
            String name = (String) paramter.nextElement();
            reqParam += name + "=" + request.getParameter(name) + "&";
        }
        if (!StrUtil.isBlank(reqParam) && reqParam.length() > 1) {
            reqParam = reqParam.substring(0, reqParam.length() - 1);
        }
        return reqParam;
    }

    /**
     * 返回数据
     *
     * @param retVal
     * @return
     */
    private static String postHandle(Object retVal) {
        if (null == retVal) {
            return "";
        }
        return JSON.toJSONString(retVal);
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request
     * @return
     */
    private static String getRemoteHost(HttpServletRequest request) {
        String ip = "";
        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }
        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        // 还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
