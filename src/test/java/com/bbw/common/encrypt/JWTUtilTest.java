package com.bbw.common.encrypt;

import com.bbw.god.login.LoginPlayer;
import io.jsonwebtoken.Claims;
import org.junit.Test;

public class JWTUtilTest {

    @Test
    public void testToken() {
        LoginPlayer player = new LoginPlayer();
        player.setAccount("account");
        player.setNickName("nickname");
        player.setLoginSid(99);
        String token = JWTUtil.generateToken("suhq", player);
        Claims claims = JWTUtil.getClaims(token);
        System.out.println(JWTUtil.checkToken(claims));
        System.out.println(JWTUtil.getExtraInfo(claims, LoginPlayer.class));
    }
}