package com.bbw.common.encrypt;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * <p>JWT身份认证工具类，采用RSA加密</p>
 *
 * @author suhq
 * @date 2020-07-28 14:26
 **/
@Slf4j
public class JWTUtil {
    /** 过期时间 */
    public static final Integer EXPIRE_MINUTES = 4320;//3天
    /** token请求头的key */
    public static final String REQ_HEADER = "token";
    private static final String EXTRA_INFO_KEY = "extraInfo";
    private static String priKeyValue = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIz7LDNjaxRoUkCYqacqHLRVRf+dAZbHZzuBUyriZOiozW2HMQw5a+cWTfI3QjK9pP7e0Pm2UMUdolCCXxauq7m4Qa808x2ol7Hlk8Jbc/jk/LVWe85FC5h255zT8+/dsVGGEoTxyK0mzVDRyINeDNJmS6LQMbD8TqQl/zL+Y2ZrAgMBAAECgYBvdnp42qajiONXLRwdDV+KeE0Sjqjd9CO7WUFPC5WqmSNp8FQoDRIxr2HLGkNdaLEVzUa915Y+cnKNYrYZcdpUkjHA+YHv+o0pZ/w7o+tw1VDdfzEEhK71L404hmxghcvuN2PgsLI62xJWiN/CmsMuEY6NvEReWZ0yiO081rElcQJBAPZNjWMIlRNx41Pjsq6CuKsImqRvHHpWeknPwFxansnhaqoTE2txHxPoDxKiJlOJZQKKO5vGr5VB6X2tPUsNFxMCQQCSiBlTYakBdgD6VSPlmzm1+vKw1OawHVJldszpK5H6GokGXlBBqtT4gGj+gyWFNe3VSeRoIZ0r7BXi1VzROSZJAkEA7M1ZDwr0UPKhLklvxEpYA9BM5aUSCyjTf92mwuQ5YD1CUOvwMs9aosfsneyZzpz9KXj/oGBg9a6eLz98+4hFUwJALCfxIcxLJpKNxuIDODynjmw52AnAHpAndUXwh89GZQy7//xJyRAWr2/as9+HXfYbXmuu9aYze7nf+oP7PV0waQJBAKg0TRPCxNinAqnhh2mbNMZKyOobRKA3K5enFgm2u0RsfO93UYpwf02fvVtWR0Bwo48gfGH3XM7fdVH8ddofLwg=";
    private static String pubKeyValue = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCM+ywzY2sUaFJAmKmnKhy0VUX/nQGWx2c7gVMq4mToqM1thzEMOWvnFk3yN0IyvaT+3tD5tlDFHaJQgl8Wrqu5uEGvNPMdqJex5ZPCW3P45Py1VnvORQuYduec0/Pv3bFRhhKE8citJs1Q0ciDXgzSZkui0DGw/E6kJf8y/mNmawIDAQAB";
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    static {

        try {
            RSAEncrypt encrypt = new RSAEncrypt();
            encrypt.loadPrivateKey(priKeyValue);
            encrypt.loadPublicKey(pubKeyValue);
            privateKey = encrypt.getPrivateKey();
            publicKey = encrypt.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Token
     *
     * @param tokenId       用户身份
     * @param extraInfo     生成用户token的同时，可附加的额外信息
     * @return
     */
    public static String generateToken(String tokenId, Object extraInfo) {
        long endTime = System.currentTimeMillis() + 1000 * 60 * EXPIRE_MINUTES;
        JwtBuilder jwtBuilder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setId(tokenId)
                .setIssuedAt(DateUtil.now())
                .setExpiration(new Date(endTime))
                .signWith(SignatureAlgorithm.RS512, privateKey);
        if (null != extraInfo) {
            jwtBuilder.claim(EXTRA_INFO_KEY, JSONUtil.toJson(extraInfo));
        }
        return jwtBuilder.compact();
    }


    /**
     * 根据token获取认证信息
     *
     * @param token
     * @return
     */
    public static Claims getClaims(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            log.error("获取getClaims出错", e);
        }

        return null;
    }

    /**
     * 认证信息是否过期
     *
     * @param claims
     * @return
     */
    public static boolean isExpired(Claims claims) {
//        System.out.println(DateUtil.toDateTimeString(claims.getExpiration()));
        return claims.getExpiration().before(new Date());
    }

    /**
     * 检查Token是否合法
     *
     * @param claims 认证信息
     * @return
     */
    public static boolean checkToken(Claims claims) {
        if (null == claims) {
            return false;
        }
        if (isExpired(claims)) {
            return false;
        }
        return true;
    }

    /**
     * 获取额外信息
     *
     * @param claims
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getExtraInfo(Claims claims, Class<T> clazz) {
        if (null == claims) {
            return null;
        }
        String extraStr = claims.get(EXTRA_INFO_KEY, String.class);
        if (StrUtil.isBlank(extraStr)) {
            return null;
        }
        return JSONUtil.fromJson(extraStr, clazz);
    }
}