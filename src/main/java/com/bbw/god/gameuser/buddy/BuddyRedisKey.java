package com.bbw.god.gameuser.buddy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件类型
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 12:23
 */
@Getter
@AllArgsConstructor
public enum BuddyRedisKey {
	/**
	 * 好友列表
	 */
	FRIEND("friend"),
	/**
	 * 好友请求列表
	 */
	ASK("ask");
	private String value;
}