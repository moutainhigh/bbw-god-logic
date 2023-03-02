package com.bbw.validator.wx.minigame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.db.redis.RedisValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信小游戏访问Token服务。远程调用微信服务时，需要传递访问token。
 *
 * @author: suhq
 * @date: 2022/1/18 2:50 下午
 */
@Slf4j
@Service
public class WXAccessTokenService {
	@Autowired
	private RedisValueUtil<String> accessTokenRedisUtil;

	/**
	 * 获取微信访问token
	 *
	 * @return
	 */
	public String getAccessToken() {
		String accessTokenKey = getKey();
		return accessTokenRedisUtil.get(accessTokenKey);
	}

	/**
	 * 刷新accessToken(加Redis锁，线程安全)
	 *
	 * @return
	 */
	@RedisLock(key = "game:var:wx:accessToken:refreshLock", tryInterval = 200L)
	public String refreshAccessToken() {
		// 前往微信验证
		String url = buildRefreshUrl();
		String result = HttpClientUtil.doGet(url);

		JSONObject jsResult = JSON.parseObject(result);
		// 检查验证结果
		boolean isSuccess = jsResult.containsKey("access_token");
		if (!isSuccess) {
			log.error("refreshAccessToken出错，错误信息：{},{}", jsResult.getInteger("errcode"), jsResult.getString("errmsg"));
			return null;
		}
		//更新accessToken
		String accessTokenKey = getKey();
		String accessToken = jsResult.getString("access_token");
		int expiresIn = jsResult.getInteger("expires_in");
		accessTokenRedisUtil.set(accessTokenKey, accessToken, (long) expiresIn);
		return accessToken;
	}


	/**
	 * 构建acccessToken刷新地址
	 *
	 * @return
	 */
	private String buildRefreshUrl() {
		StringBuilder urlSb = new StringBuilder();
		urlSb.append(WXMiniGameConfig.BASE_URL);
		urlSb.append(WXMiniGameConfig.REFRESH_ACCESS_TOKEN_API);
		urlSb.append("?grant_type=client_credential");
		urlSb.append("&appid=" + WXMiniGameConfig.APP_ID);
		urlSb.append("&secret=" + WXMiniGameConfig.SECRET);

		return urlSb.toString();
	}

	/**
	 * 缓存accessToken的key
	 *
	 * @return
	 */
	private String getKey() {
		return "game:var:wx:accessToken";
	}
}
