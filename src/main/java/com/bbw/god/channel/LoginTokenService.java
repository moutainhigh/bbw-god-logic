package com.bbw.god.channel;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.GameRedisKey;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-14 18:09
 */
@Slf4j
@Service
public class LoginTokenService {
	@Autowired
	private RedisValueUtil<String> reidsToken;//玩家token

	public String getLoginToken(String loginId) {
		String token = "bbwToken" + UUID.randomUUID().toString().replace("-", "");
		String loginTokenKey = getLoginTokenKey(loginId);
		reidsToken.set(loginTokenKey, token);
		return token;
	}

	/**
	 * 令牌验证
	 * @param accountId: account.id
	 * @param token
	 * @return
	 */
	public boolean validToken(String loginId, String token) {
		String loginTokenKey = getLoginTokenKey(loginId);
		String storeToken = reidsToken.get(loginTokenKey);
		if (null == token) {
			log.error("account.id=[{}]的登录验证失败!没有提交令牌参数。", loginId);
			return false;
		}
		boolean b = StrUtil.equals(token, storeToken);
		if (!b) {
			log.error("account.id=[{}]的登录验证失败!提交令牌[{}],有效令牌[{}]", loginId, token, storeToken);
		}
		return b;
	}

	private String getLoginTokenKey(String accountId) {
		return GameRedisKey.getRunTimeVarKey("loginToken") + GameRedisKey.SPLIT + accountId;
	}
}
