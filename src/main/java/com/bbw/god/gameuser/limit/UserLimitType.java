package com.bbw.god.gameuser.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单个玩家角色某一区服行为限制类型
 * 
 * @author suhq
 * @date 2019年3月17日 下午1:05:27
 */
@Getter
@AllArgsConstructor
public enum UserLimitType {
	LOGIN_LIMIT("限制用户登录时间", 10),
	TALK_LIMIT("限制用户聊天时间", 20);

	private String name;
	private int value;

	public static UserLimitType fromValue(int value) {
		for (UserLimitType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

}
