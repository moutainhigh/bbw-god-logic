package com.bbw.god.gameuser.achievement;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 成就状态
 * 
 * @author suhq
 * @date 2019年2月21日 上午11:18:46
 */
@Getter
@AllArgsConstructor
public enum AchievementStatusEnum {

	NO_ACCOMPLISHED("未达成", 0),
	ACCOMPLISHED("已达成", 1),
	AWARED("已领取", 10);

	private final String name;
	private final int value;

	public static AchievementStatusEnum fromValue(int value) {
		for (AchievementStatusEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
