package com.bbw.god.security.limiter;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 单一请求频率限制
 *
 * @author: suhq
 * @date: 2021/12/2 1:02 下午
 */
@Slf4j
public abstract class SingleRequestTimeLimiter {
    protected String requestUri;
    protected Map<String, Long> requestTimeMap = new HashMap<>();
    /** 一个玩家作用域的同一个请求的最小间隔时间(ms) */
    protected int minRequestInterval = 100;

    public boolean isPass(String sender) {
        long now = System.currentTimeMillis();
        boolean isPass = false;
        synchronized (sender) {
            Long lastRequestTime = requestTimeMap.getOrDefault(sender, 0L);
            long interval = now - lastRequestTime;
            isPass = interval > minRequestInterval;
            requestTimeMap.put(sender, now);
        }
        return isPass;
    }

    public boolean isMatch(String srcUri) {
        return srcUri.contains(requestUri);
    }
}
