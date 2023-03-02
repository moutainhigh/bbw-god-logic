package com.bbw.god.security.limiter;

import com.alibaba.fastjson.JSONObject;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.HttpRequestUtil;
import com.bbw.common.IpUtil;
import com.bbw.common.LM;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.security.AbstractInterceptor;
import com.bbw.god.server.ServerStatus;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 请求限制（请求频率、请求ip）
 * <br/> 这个拦截器要排在 TokenInterceptor 后面
 *
 * @author: suhq
 * @date: 2021/11/25 2:56 下午
 */
@Slf4j
@Component
public class RequestLimiterInterceptor extends AbstractInterceptor {
    @Autowired
    private RoleRequestTimeLimiter roleRequestTimeLimiter;
    @Autowired
    private LoginRequestTimeLimiter loginRequestTimeLimiter;
    @Autowired
    private TokenRefreshRequestTimeLimiter tokenRefreshRequestTimeLimiter;
    @Autowired
    private App app;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //不需要过滤
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //登录限制
        if (loginRequestTimeLimiter.isMatch(request.getRequestURI())) {
            if (!loginRequestTimeLimiter.isPass(request.getParameter("email"))) {
                throw ExceptionForClientTip.fromi18nKey("login.is.logining");
            }
            return true;
        }
        //token刷新限制
        if (tokenRefreshRequestTimeLimiter.isMatch(request.getRequestURI())) {
            if (!tokenRefreshRequestTimeLimiter.isPass(request.getParameter("uid"))) {
                tooOften(response);
                return false;
            }
            return true;
        }
        //管理后台限制
        if (!app.runAsDev() && !app.runAsTest() && request.getRequestURI().contains("/gm/")) {
            String ip = IpUtil.getIpAddr(request);
            CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
            if (!cfgGame.getGmWhiteIps().contains(ip)) {
                log.error("{}非法访问管理接口！{}", ip, HttpRequestUtil.getLogString(request));
                throw CoderException.high("无法访问" + request.getRequestURI());
            }
        }
        if (isNotNeedToken(request.getRequestURI())) {
            return true;
        }
        LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);

        // 服务器维护时，限制其他玩家登录
        CfgServerEntity loginServer = Cfg.I.get(player.getServerId(), CfgServerEntity.class);
        CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
        // 非白名单的人员需要检测服务器的状态
        boolean isWhiteAccount = gameConfig.getWhiteAccounts().contains(player.getAccount());
        String ip = IpUtil.getIpAddr(request);
        boolean isWhiteIp = gameConfig.getGmWhiteIps().contains(ip);
        boolean isNeedCheckServer = !isWhiteAccount && !isWhiteIp;
        if (isNeedCheckServer && loginServer.getServerStatus() == ServerStatus.MAINTAINING) {
            String msgTpl = LM.I.getMsg("server.maintaining");
            String tipDetail = String.format(msgTpl, DateUtil.toString(loginServer.getMtEndTime(), "H:mm"));
            relogin(response, tipDetail);
            return false;
        }
        //请求频率限制
        if (!roleRequestTimeLimiter.isPass(player.getUid(), request)) {
            log.warn(player.getUid() + "请求" + request.getRequestURI() + "过于频繁");
            tooOften(response);
            return false;
        }
        return true;
    }

    /**
     * 告知客户端请求过于频繁
     *
     * @param response
     */
    private void tooOften(HttpServletResponse response) {
        response.setContentType("text/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");// "text/html;charset=utf-8"
        JSONObject js = new JSONObject();
        js.put("message", LM.I.getMsg("client.request.too.often"));
        js.put("res", 1);
        @Cleanup
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(js.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
