package com.bbw.god.security.limiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * token刷新请求频率限制
 *
 * @author: suhq
 * @date: 2021/12/2 1:02 下午
 */
@Slf4j
@Service
public class TokenRefreshRequestTimeLimiter extends SingleRequestTimeLimiter {
    public TokenRefreshRequestTimeLimiter() {
        this.requestUri = "gu!refreshToken";
        this.minRequestInterval = 100;
    }
}
