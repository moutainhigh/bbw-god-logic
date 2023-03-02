package com.bbw.god.activity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动范围
 * 
 * @author suhq
 * @date 2019年4月8日 下午5:42:01
 */
@Getter
@AllArgsConstructor
public enum ActivityScopeEnum {

	GAME("整个游戏", 10),
	SERVER("区服", 20);

	private String name;
	private int value;

	public static ActivityScopeEnum fromValue(int value) {
		for (ActivityScopeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
