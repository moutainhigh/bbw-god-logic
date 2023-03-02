package com.bbw.god.activity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动分组
 * 
 * @author suhq
 * @date 2019年3月3日 下午11:20:32
 */
@Getter
@AllArgsConstructor
public enum ActivityParentTypeEnum {

	BUILD_IN_ACTIVITY("活动", 10),
	BUILD_IN_WELFARE("福利", 20),
	NO_UI_ACTIVITY("没有界面的活动", 30),
	FIRST_RECHARGE_ACTIVITY("首冲活动", 40),
	HOLIDAY_ACTIVITY("节日活动", 50),
	HOLIDAY_ACTIVITY_51("节日活动", 51),
	HOLIDAY_ACTIVITY_52("节日活动", 52),
	NORMAL_HOLIDAY_ACTIVITY("普通节日活动", 55),
	WONDERFUL_ACTIVITY("精彩活动", 60),
	HERO_BACK_ACTIVITY("英雄回归活动", 70),
	MALL_ACTIVITY("商铺活动", 80),
	NEWER_WELFARE("新手福利", 90),
	COMBINED_SERVICE("合服活动", 100),
	WORLD_CUP_ACTIVITY("世界杯活动", 110),
	;

	private final String name;
	private final int value;

	public static ActivityParentTypeEnum fromValue(int value) {
		for (ActivityParentTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
