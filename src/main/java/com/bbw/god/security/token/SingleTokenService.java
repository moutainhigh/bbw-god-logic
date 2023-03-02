package com.bbw.god.security.token;

import com.bbw.common.DateUtil;
import com.bbw.common.StrUtil;
import com.bbw.common.encrypt.JWTUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.login.LoginPlayer;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 唯一有效登录token
 *
 * @author: suhq
 * @date: 2021/11/25 5:19 下午
 */
@Slf4j
@Service
public class SingleTokenService {
    private static final String KEY = RedisKeyConst.RUNTIME_KEY + RedisKeyConst.SPLIT + "uidMapToken";
    @Autowired
    private RedisHashUtil<Long, AuthToken> uidMapToken;

    /**
     * 登录token
     *
     * @param uid
     * @param player
     */
    public AuthToken generateToken(Long uid, LoginPlayer player) {
        String tokenId = generateTokenId(uid);
        Date expiredDate = DateUtil.addMinutes(DateUtil.now(), JWTUtil.EXPIRE_MINUTES);
        String token = JWTUtil.generateToken(tokenId, player);
        AuthToken authToken = new AuthToken(token, expiredDate);
        uidMapToken.putField(KEY, uid, authToken);
        return authToken;
    }

    /**
     * 获取当前token
     *
     * @param uid
     * @return
     */
    public AuthToken getCurToken(long uid) {
        return uidMapToken.getField(KEY, uid);
    }

    /**
     * 每个角色只能有一个有效的token
     *
     * @param claims
     * @return
     */
    public boolean isTokenValid(String clientToken, Claims claims) {
        try {
            if (null == claims) {
                return false;
            }
            String tokenId = claims.getId();
            if (StrUtil.isBlank(tokenId)) {
                return false;
            }
            Long uid = getUidByTokenId(tokenId);
            AuthToken authToken = getCurToken(uid);
            if (null == authToken) {
                return false;
            }
            if (!clientToken.equals(authToken.getToken())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 生成tokenId
     *
     * @param uid
     * @return
     */
    private String generateTokenId(Long uid) {
        return uid + "_" + System.currentTimeMillis();
    }

    /**
     * 根据tokenId得到玩家ID
     *
     * @param tokenId
     * @return
     */
    private Long getUidByTokenId(String tokenId) {
        return Long.parseLong(tokenId.split("_")[0]);
    }

}
