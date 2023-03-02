package com.bbw.god.security.param;

import com.bbw.cache.LocalCache;
import com.bbw.common.JSONUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.ShareCodeUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 客户端通讯令牌集合
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-06 18:21
 */
@Slf4j
@Service
public class SecurityParamService {
	private static final String CACHE_KEY = GameUserRedisUtil.TOKEN_CACHE_KEY;//缓存的redis key
	private static final long TIME_OUT = 60 * 60 * 24;//24小时

	@Autowired
	private RedisValueUtil<SecurityParam> valueRedis;//玩家

	/**
	 * 创建令牌
	 *
	 * @param uid
	 * @param version
	 * @return
	 */
	public SecurityParam build(Long uid, long version) {
		SecurityParam tk = LocalCache.getInstance().get(CACHE_KEY, uid.toString());
		if (null == tk) {
			tk = new SecurityParam();
		} else {
			tk.getTokens().clear();
		}
		tk.setUid(uid);
		tk.setVersion(version);
		int size = 50;
		long l = System.currentTimeMillis();
		l = l / 1024 / 256 + uid / 1896561578 + uid / l;
		for (int i = 0; i < size; i++) {
			l += PowerRandom.getRandomBySeed(1024 * 256);
			String tokenCode = ShareCodeUtil.toSerialCode(l);
			tk.getTokens().add(tokenCode);
		}
		LocalCache.getInstance().put(CACHE_KEY, uid.toString(), tk, TIME_OUT);
		String redisKey = UserRedisKey.getRunTimeVarKey(uid, CACHE_KEY);
		valueRedis.set(redisKey, tk);
		valueRedis.expire(redisKey, TIME_OUT);
		return tk;
	}

	/**
	 * 验证参数是否合法和重复
	 *
	 * @param param
	 * @param version
	 * @return
	 */
	public boolean valid(RequestSecurityParams param, long version) {
		SecurityParam tk = LocalCache.getInstance().get(CACHE_KEY, param.getUid().toString());
		if (null == tk || tk.getVersion() < version) {
			//本地没有数据，从redis中获取最新
			String redisKey = UserRedisKey.getRunTimeVarKey(param.getUid(), CACHE_KEY);
			tk = valueRedis.get(redisKey);
			if (null == tk) {
				return false;
			}
			LocalCache.getInstance().put(CACHE_KEY, param.getUid().toString(), tk, TIME_OUT);
		}
		//本地有数据，先利用本地数据判定，如果
		boolean isValid = validToken(tk, param);
		//更新
		update(tk, param);
		return isValid;
	}

	private boolean validToken(SecurityParam tk, RequestSecurityParams param) {

		if (param.getTimestamp() == tk.getLastTimestamp() && tk.getTimes() >= 5) {
			log.error(" 不通过。uid={},前后3次请求的timestamp相同。timestamp={},uri={}", tk.getUid(), param.getTimestamp(), param.getUri());
			return false;
		}
		if (StrUtil.equals(tk.getLast1Code(), param.getTokenCode())) {
			log.error("不通过。uid={},前后2次请求的验证码相同。timestamp={},code={},uri={}", tk.getUid(), param.getTimestamp(), param.getTokenCode(), param.getUri());
			return false;
		}
		//本次代码与前两次对比是否相同
		if (StrUtil.equals(tk.getLast1Code(), param.getTokenCode()) && StrUtil.equals(tk.getLast2Code(), param.getTokenCode())) {
			log.error("不通过。uid={},连续3次验证码相同。code={},uri={}", tk.getUid(), param.getTokenCode(), param.getUri());
			return false;
		}
		//校验代码是否合法
		boolean pass = tk.getTokens().contains(param.getTokenCode());
		if (!pass) {
			log.error("不通过。uid={},令牌不合法。code={},allcode={},uri={}", tk.getUid(), param.getTokenCode(), JSONUtil.toJson(tk.getTokens()), param.getUri());
		}
		return pass;
	}

	private void update(SecurityParam tk, RequestSecurityParams param) {
		tk.updateCode(param.getTokenCode());
		tk.updateTimestamp(param.getTimestamp());
		tk.updateUri(param.getUri());
		LocalCache.getInstance().put(CACHE_KEY, param.getUid().toString(), tk, TIME_OUT);
	}

}
