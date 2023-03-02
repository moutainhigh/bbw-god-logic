package com.bbw.god.security;

import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 拦截器抽象类
 *
 * @author: suhq
 * @date: 2021/11/25 2:16 下午
 */
public abstract class AbstractInterceptor implements HandlerInterceptor {
    private static String[] NOT_NEED_TOKEN_URI = {"log.html", "docs.html", "/webjars/", "/v2/",
            "/account!login", "/account!needLogin", "/gu!createRole", "gu!randomName", "gu!refreshToken",
            "product!notify", "product!canbuy", "webProduct!listProducts",
            "/combatPVP!", "/chat/", "/helperMessage", "/gm/", "/fsfight!", "/error", "/coder/",
            "health.html", "serverlogs.html", "/statics", "/druid/",
            "/getLoginData"};
    private static String[] IGNORE_SECRUITY_PARAM_URI = {"gu!gainUserInfo", "gu!updateOnlineStatus", "gu!gainNewInfo", "maou" +
            "!listRankings", "maou!gainInfoIncludeRankings"};

    /**
     * 告知客户端重新登录
     *
     * @param response
     * @param msg
     * @throws IOException
     */
    public void relogin(HttpServletResponse response, String msg) {
        response.setContentType("text/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        JSONObject js = new JSONObject();
        js.put("message", msg);
        js.put("res", 10);
        @Cleanup
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(js.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 无需权限token的请求
     *
     * @param uri
     * @return
     */
    public boolean isNotNeedToken(String uri) {
        for (String s : NOT_NEED_TOKEN_URI) {
            if (uri.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 忽略安全参数校验的请求
     *
     * @param uri
     * @return
     */
    protected boolean isIgnoreSecurityParam(String uri) {
        for (String s : IGNORE_SECRUITY_PARAM_URI) {
            if (uri.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
