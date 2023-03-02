package com.bbw.god.security.limiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 角色请求频率限制请求
 *
 * @author suhq
 * @date 2020-09-10 11:41
 **/
@Slf4j
@Service
public class RoleRequestTimeLimiter {
    private Map<Long, Map<String, Long>> roleRequestTimeMap = new HashMap<>();
    /** 一个玩家作用域的同一个请求的最小间隔时间 */
    private static final int MIN_REQUEST_INTERVAL = 125;
    private static final int MIN_STRICT_REQUEST_INTERVAL = 1250;
    private static String[] IGNORE_URI = {"snatchTreasure!enterSnatchTreasure", "guild!infoOverview", "gu!gainUserInfo", "gu!gainNewInfo", "guild!joinGuild", "maou!gainMaou", "maou!gainInfoIncludeRankings", "maou!gainRemainBlood", "byPalace!enterBYPalace", "gu!gainFightInfo"};
    private static String[] STRICT_URI = {"combat!autoEndCombat"};

    public boolean isPass(long roleId, HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("list") || uri.contains("get")) {
            return true;
        }
        uri = uri.substring(10);
        for (String ignoreUri : IGNORE_URI) {
            if (ignoreUri.equals(uri)) {
                return true;
            }
        }
        long now = System.currentTimeMillis();
        long lastRequestTime = 0;
        Map<String, Long> lastRequestsTime = roleRequestTimeMap.get(roleId);
        if (lastRequestsTime == null) {
            lastRequestsTime = new HashMap<>();
            roleRequestTimeMap.put(roleId, lastRequestsTime);
        } else {
            lastRequestTime = lastRequestsTime.getOrDefault(uri, 0L);
        }
        long interval = now - lastRequestTime;
        boolean isPass = interval > getRequestInterval(uri);
        lastRequestsTime.put(uri, now);

        return isPass;
    }

    private long getRequestInterval(String uri) {
        for (String strictUri : STRICT_URI) {
            if (strictUri.equals(uri)) {
                return MIN_STRICT_REQUEST_INTERVAL;
            }
        }
        return MIN_REQUEST_INTERVAL;
    }


    public void resetLimit(long roleId) {
        roleRequestTimeMap.remove(roleId);
    }
}
