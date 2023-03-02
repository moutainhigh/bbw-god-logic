package com.bbw.god.security.param;

import com.alibaba.fastjson.JSONObject;
import com.bbw.common.HttpRequestUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.AppSecurityException;
import com.bbw.exception.SecurityLevel;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.security.AbstractInterceptor;
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
 * 安全参数检查
 * <br/> 这个拦截器要排在 TokenInterceptor 后面
 */
@Slf4j(topic = "godSecurity")
@Component
public class SecurityParamInterceptor extends AbstractInterceptor {

    private static final String tkKey = "tk";
    private static final String tpl = " 缺少 %s 参数!";
    @Autowired
    private SecurityParamService securityParamService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //不需要过滤
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (isNotNeedToken(request.getRequestURI()) || isIgnoreSecurityParam(request.getRequestURI())) {
            return true;
        }
        //检查时间戮
        String tkStr = request.getParameter(tkKey);
        if (null == tkStr) {
            String msg = String.format(tpl, tkKey);
            throw new AppSecurityException(msg, SecurityLevel.HIGH);
        }
        long timestamp = StrUtil.getLong(tkStr, 0);
        String pnkey = SecurityParamKey.idToParamKey(timestamp);
        String tokenCode = request.getParameter(pnkey);
        if (null == tokenCode) {
            String msg = String.format(tpl, "安全");
            throw new AppSecurityException(msg, SecurityLevel.HIGH);
        }

        //验证安全参数
        LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
        RequestSecurityParams param = new RequestSecurityParams();
        param.setUid(player.getUid());
        param.setTokenCode(tokenCode);
        param.setTimestamp(System.currentTimeMillis());
        param.setUri(HttpRequestUtil.getLogString(request));
        long version = player.getTokenVersion();
        // 验证不通过
        if (!securityParamService.valid(param, version)) {
            log.error("安全校验不通过，判定uid={},的玩家作弊!{}", player.getUid(), param.getUri());
            redo(response);
            return false;
        }
        return true;
    }

    /**
     * 告知客户端让玩家重试请求
     *
     * @param response
     */
    private void redo(HttpServletResponse response) {
        response.setContentType("text/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");// "text/html;charset=utf-8"
        JSONObject js = new JSONObject();
        js.put("message", "请重试");
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
