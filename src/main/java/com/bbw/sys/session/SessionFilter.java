package com.bbw.sys.session;

import com.alibaba.fastjson.JSONObject;
import com.bbw.App;
import com.bbw.common.*;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.security.limiter.LoginRequestTimeLimiter;
import com.bbw.god.security.limiter.RoleRequestTimeLimiter;
import com.bbw.god.security.param.RequestSecurityParams;
import com.bbw.god.security.param.SecurityParamKey;
import com.bbw.god.security.param.SecurityParamService;
import com.bbw.god.server.ServerStatus;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 20:45
 */
@Slf4j
public class SessionFilter implements Filter {
    private static String[] IGNORE_URI = {"log.html", "docs.html", "/webjars/", "/v2/", "/combatPVP!", "/account!login", "/account!needLogin",
            "/gu!createRole", "product!notify", "product!canbuy", "webProduct!listProducts", "/error", "/coder/",
            "/chat/", "/gm/", "/fsfight!", "health.html", "serverlogs.html", "/statics", "/druid/", "gu!randomName",
            "/getLoginData"};
    private static String[] NO_SECRUITY_URI = {"gu!gainUserInfo", "gu!updateOnlineStatus", "gu!gainNewInfo", "maou" +
            "!listRankings", "maou!gainInfoIncludeRankings"};
    @Autowired
    private SecurityParamService securityParamService;
    @Autowired
    private SingleUserService singleUserService;
    @Autowired
    private RoleRequestTimeLimiter roleRequestTimeLimiter;
    @Autowired
    private LoginRequestTimeLimiter loginRequestTimeLimiter;
    @Autowired
    private App app;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            setCorsHeaders(request, response);
            if (request.getRequestURI().endsWith("healthCheck")) {// ????????????????????????
                request.setAttribute("health", "health");// ???localhost_access_log??????????????????????????????condition="health"?????????"healthCheck"?????????????????????
            } else {
                // if (app.runAsDev() || Cfg.I.getUniqueConfig(CfgGame.class).getGmWhiteIps().contains(IpUtil.getIpAddr(request))) {
                // System.out.println(DateUtil.toDateTimeString(DateUtil.now()) + ",request:" + request.getRequestURI() + "/" +
                // HttpClientNewUtil.getRequestParams(request) + ",session???" + request.getSession().getId());
                // }
                RequestContextHolder.setValue(request.getHeader("language"));
                if (!isNeedFilter(request.getRequestURI())) {
                    HttpSession session = request.getSession();
                    Object obj = session.getAttribute(session.getId());
//                    log.info("sessionfilter ip={},sessiid={}", IpUtil.getIpAddr(request), session.getId());
//                    System.out.println("sessionfilter ip=" + IpUtil.getIpAddr(request) + ",sessiid=" + session.getId());
                    if (null == obj) {// ?????????
//                        GodRequestAspect.dolog(request, JSONUtil.toJson(Rst.businessFAIL("??????????????????????????????????????????")));
                        relogin(session, request, response, -1L, LM.I.getMsg("login.to.relogin.by.leave"));
                        return;
                    }
                    LoginPlayer player = (LoginPlayer) session.getAttribute(session.getId());
                    if (!roleRequestTimeLimiter.isPass(player.getUid(), request)) {
                        log.warn(player.getUid() + "??????" + request.getRequestURI() + "????????????");
                        tooOften(request, response);
                        return;
                    }
                    // ?????????????????????????????????????????????
                    CfgServerEntity loginServer = Cfg.I.get(player.getServerId(), CfgServerEntity.class);
                    CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
                    // ???????????????????????????????????????????????????
                    if (!gameConfig.getWhiteAccounts().contains(player.getAccount()) && !loginServer.isDevTest()) {
                        ServerStatus serverStatus = loginServer.getServerStatus();
                        if (serverStatus == ServerStatus.MAINTAINING) {
                            String msgTpl = LM.I.getMsg("server.maintaining");
                            String tipDetail = String.format(msgTpl, DateUtil.toString(loginServer.getMtEndTime(), "H:mm"));
                            relogin(session, request, response, -1L, tipDetail);
                            return;
                        }
                    }

                    try {
                        if (!ignoreSecurityCheck(request.getRequestURI())) {
                            // ????????????
                            long timestamp = StrUtil.getLong(request.getParameter("tk"), 0);
                            String pnkey = SecurityParamKey.idToParamKey(timestamp);
                            String tokenCode = request.getParameter(pnkey);
                            if (!app.runAsDev() && null == tokenCode) {
                                log.error("???????????????,??????tokenCode???????????????uid={},???????????????!{}", player.getUid(), HttpRequestUtil.getLogString(request));
                                relogin(session, request, response, player.getUid(), LM.I.getMsg("login.to.relogin.by.arg.error"));
                                return;
                            }
                            RequestSecurityParams param = new RequestSecurityParams();
                            param.setUid(player.getUid());
                            param.setTokenCode(tokenCode);
                            param.setTimestamp(timestamp);
                            param.setUri(HttpRequestUtil.getLogString(request));
                            long version = player.getTokenVersion();
                            // ???????????????
                            if (!app.runAsDev() && !securityParamService.valid(param, version)) {
                                log.error("??????????????????????????????uid={},???????????????!{}", player.getUid(), param.getUri());
                                relogin(session, request, response, player.getUid(), LM.I.getMsg("login.to.relogin.by.action.exception"));
                                return;
                            }
                            String sessionId = singleUserService.getSessionId(player.getUid());
                            if (!StrUtil.equals(session.getId(), sessionId)) {
                                log.error("??????session?????????uid={},???????????????!{}", player.getUid(), param.getUri());
                                relogin(session, request, response, player.getUid(), LM.I.getMsg("login.to.relogin"));
                                return;
                            }
                        }
                        LM.I.setLocal(player.getUid(), request.getHeader("language"));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        relogin(session, request, response, player.getUid(), LM.I.getMsg("login.to.relogin"));
                    }
                } else if (request.getRequestURI().contains("account!login")) {
                    if (!loginRequestTimeLimiter.isPass(request.getParameter("email"))) {
                        throw ExceptionForClientTip.fromi18nKey("login.is.logining");
                    }
                } else if (request.getRequestURI().contains("/gm/")) {
                    String ip = IpUtil.getIpAddr(request);
                    CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
                    if (!(cfgGame.getGmWhiteIps().contains(ip) || app.runAsDev() || app.runAsTest())) {
                        log.error("{}???????????????????????????{}", ip, HttpRequestUtil.getLogString(request));
                        throw CoderException.high("????????????" + request.getRequestURI());
                    }

                }
            }
            filterChain.doFilter(request, response);
        } finally {
            RequestContextHolder.clear();
        }
    }

    private void relogin(HttpSession session, HttpServletRequest request, HttpServletResponse response, Long uid, String msg) throws IOException {
        try {
            if (null != session) {
                session.setAttribute(session.getId(), null);
                session.invalidate();
            }
            if (uid > 0) {
                singleUserService.removeSessionId(uid);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        response.reset();
        setCorsHeaders(request, response);
        response.setContentType("text/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");// "text/html;charset=utf-8"
        JSONObject js = new JSONObject();
        js.put("message", msg);
        js.put("res", 10);
        @Cleanup
        PrintWriter out = response.getWriter();
        out.write(js.toString());
    }

    private void tooOften(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(request, response);
        response.setContentType("text/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");// "text/html;charset=utf-8"
        JSONObject js = new JSONObject();
        js.put("message", LM.I.getMsg("client.request.too.often"));
        js.put("res", 1);
        @Cleanup
        PrintWriter out = response.getWriter();
        out.write(js.toString());
    }

    public boolean isNeedFilter(String uri) {
        for (String s : IGNORE_URI) {
            if (uri.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean ignoreSecurityCheck(String uri) {
        if (app.runAsDev()) {
            return true;
        }
        for (String s : NO_SECRUITY_URI) {
            if (uri.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin")); //  ???????????????????????????????????????
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("XDomainRequestAllowed", "1");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN");
    }
}
