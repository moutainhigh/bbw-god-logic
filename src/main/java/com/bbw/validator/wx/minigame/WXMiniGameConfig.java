package com.bbw.validator.wx.minigame;

/**
 * 微信小程序配置
 *
 * @author: suhq
 * @date: 2022/1/18 2:48 下午
 */
public class WXMiniGameConfig {
	/** 平台ID */
	public static final Integer CHANNEL_ID = 80;
	/** 平台码 */
	public static final String CHANNEL_CODE = "80";
	/** 商户ID */
	public static final String OFFER_ID = "1450032745";
	/** 应用标识 */
	public static final String APP_ID = "wx16356b83bb990b11";
	/** 应用密钥 */
	public static final String SECRET = "11a5ef409a836914dfcdfec20a517a05";
	/**
	 * 请求参数签名密钥
	 * 沙箱AppKey：LeaUDpeo81gg1EmZJtUEwU7likZAVQeH
	 * 现网AppKey：A0i4oA48GZxpNgXq9aN6UtwyzA2zEmfJ
	 */
	public static final String APP_KEY = "A0i4oA48GZxpNgXq9aN6UtwyzA2zEmfJ";
	/** 微信服务地址 */
	public static final String BASE_URL = "https://api.weixin.qq.com";
	/** 刷新远程访问微信的accessToken的接口 */
	public static final String REFRESH_ACCESS_TOKEN_API = "/cgi-bin/token";
	/** 违法违规内容 */
	public static final String MSG_SEC_CHECK = "/wxa/msg_sec_check";
}
