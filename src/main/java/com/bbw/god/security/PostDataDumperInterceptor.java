package com.bbw.god.security;

import com.bbw.god.login.LoginPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * local_access记录额外数据
 *
 * @author: suhq
 * @date: 2021/12/24 2:13 下午
 */
@Slf4j
@Component
public class PostDataDumperInterceptor extends AbstractInterceptor {
    private static List<String> URL_WITHOUT_ACCESS_LOG = Arrays.asList("healthCheck", "gu!gainNewInfo", "fsHepler!listTask");

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        if (URL_WITHOUT_ACCESS_LOG.stream().anyMatch(tmp -> uri.contains(tmp))) {
            //在localhost_access_log的配置项上，conditionUnless="junk"，这样对应的请求就不被记录
            request.setAttribute("junk", "1");
        } else {
            StringBuilder output = new StringBuilder();
            LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
            // 已登录
            if (null != player) {
                StringBuilder sb = new StringBuilder();
                sb.append(player.getUid());
                sb.append("&");
                sb.append(player.getServerId());
                sb.append("&");
                sb.append(player.getChannelId());
                output.append(sb);
            }
            request.setAttribute("postdata", output);
        }
    }
}