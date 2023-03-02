package com.bbw.god.login;

import com.bbw.god.gameuser.GameUser;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-28 08:48
 */
@Data
public class LoginInfo {
	private GameUser user;
	private String ip;
	private String token;
	private String pushToken;
	private String deviceId;

	public LoginInfo(GameUser gu, String ip, String pushToken) {
		this.user = gu;
		this.ip = ip;
		this.pushToken = pushToken;
	}

	public LoginInfo(GameUser gu, String ip, String token, String deviceId) {
		this.user = gu;
		this.ip = ip;
		this.token = token;
		this.deviceId = deviceId;
	}
}
