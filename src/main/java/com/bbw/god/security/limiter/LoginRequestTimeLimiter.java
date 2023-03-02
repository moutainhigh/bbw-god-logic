package com.bbw.god.security.limiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录请求频率限制请求
 *
 * @author suhq
 * @date 2020-09-10 11:41
 **/
@Slf4j
@Service
public class LoginRequestTimeLimiter extends SingleRequestTimeLimiter {
    public LoginRequestTimeLimiter() {
        this.requestUri = "account!login";
        this.minRequestInterval = 4000;
    }
}
