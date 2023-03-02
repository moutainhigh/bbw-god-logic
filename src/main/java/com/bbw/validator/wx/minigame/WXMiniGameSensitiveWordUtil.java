package com.bbw.validator.wx.minigame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小游戏敏感词检测
 *
 * @author: suhq
 * @date: 2022/1/27 6:23 下午
 */
@Slf4j
@Service
public class WXMiniGameSensitiveWordUtil {
	private static WXAccessTokenService wxAccessTokenService = SpringContextUtil.getBean(WXAccessTokenService.class);

	/**
	 * 微信小游戏敏感词检测
	 *
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static boolean isPass(String openId, String content) {

		JSONObject checkResult = sendCheckUrl(openId, content);
		boolean isRequetSuccess = checkResult.containsKey("errcode") && checkResult.getInteger("errcode") == 0;
		//检查失败
		if (!isRequetSuccess) {
			return false;
		}
		JSONObject result = checkResult.getJSONObject("result");
		return "pass".equals(result.getString("suggest"));
	}

	/**
	 * 发送微信敏感词请求
	 *
	 * @param openId
	 * @param content
	 * @return
	 */
	private static JSONObject sendCheckUrl(String openId, String content) {
		String accessToken = getAccessToken();
		String checkUrl = buildUrl(WXMiniGameConfig.BASE_URL, WXMiniGameConfig.MSG_SEC_CHECK, accessToken);
		Map<String, Object> params = buildParams(openId, content, accessToken);
		String postData = JSONUtil.toJson(params);
		System.out.println("send checkWord data:" + postData);
		String httpResult = HttpClientUtil.doPostJson(checkUrl, postData);
		System.out.println("send checkWord result:" + httpResult);
		JSONObject result = JSON.parseObject(httpResult);
		return result;
	}

	/**
	 * 构建请求
	 *
	 * @param url
	 * @param api
	 * @param accessToken
	 * @return
	 */
	private static String buildUrl(String url, String api, String accessToken) {
		StringBuilder urlSb = new StringBuilder();
		urlSb.append(url);
		urlSb.append(api);
		urlSb.append("?access_token=");
		urlSb.append(accessToken);
		return urlSb.toString();
	}

	/**
	 * 获取访问token
	 *
	 * @return
	 */
	private static String getAccessToken() {
		String accessToken = wxAccessTokenService.getAccessToken();
		if (StrUtil.isEmpty(accessToken)) {
			accessToken = wxAccessTokenService.refreshAccessToken();
		}
		if (StrUtil.isEmpty(accessToken)) {
			throw ExceptionForClientTip.fromMsg("访问微信接口失败，请重试!");
		}
		return accessToken;
	}


	/**
	 * 构建请求参数
	 *
	 * @param openId
	 * @param content
	 * @param accessToken
	 * @return
	 */
	private static Map<String, Object> buildParams(String openId, String content, String accessToken) {
		Map<String, Object> params = new HashMap<>();
		params.put("openid", openId);
		params.put("version", 2);
		params.put("scene", 1);
		params.put("content", content);
		return params;
	}

}
