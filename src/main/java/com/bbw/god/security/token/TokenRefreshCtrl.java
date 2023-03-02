package com.bbw.god.security.token;

import com.bbw.common.DateUtil;
import com.bbw.common.encrypt.JWTUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.login.LoginPlayer;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端发送业务请求前检查token有效期，如果token在未来的1h内将过期，则调用该接口刷新token
 *
 * @author: suhq
 * @date: 2021/11/25 6:21 下午
 */
@Slf4j
@RestController
public class TokenRefreshCtrl extends AbstractController {
    @Autowired
    private SingleTokenService singleTokenService;

    /**
     * 刷新token
     *
     * @return
     */
    @GetMapping("gu!refreshToken")
    public RDRefreshToken refreshToken(long uid) {
        AuthToken curToken = singleTokenService.getCurToken(uid);
        long remainExpiredMinutes = DateUtil.getMinutesBetween(DateUtil.now(), curToken.getExpiredDate());
        // 客户端刷新token离过期超过60分钟，不刷新token，直接返回当前token。避免被重复刷新
        if (remainExpiredMinutes > 60) {
            RDRefreshToken rd = new RDRefreshToken(curToken.getToken(), curToken.getExpiredDate().getTime());
            return rd;
        }
        Claims claims = JWTUtil.getClaims(curToken.getToken());
        LoginPlayer loginPlayer = JWTUtil.getExtraInfo(claims, LoginPlayer.class);
        AuthToken newToken = singleTokenService.generateToken(uid, loginPlayer);
        RDRefreshToken rd = new RDRefreshToken(newToken.getToken(), newToken.getExpiredDate().getTime());
        return rd;
    }

}
