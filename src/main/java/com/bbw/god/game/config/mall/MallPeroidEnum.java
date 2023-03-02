package com.bbw.god.game.config.mall;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商城产品周期
 *
 * @author suhq
 * @date 2019年2月25日 下午3:29:20
 */
@Getter
@AllArgsConstructor
public enum MallPeroidEnum {
	FOREVER("永久", 0),
	PER_DAY("每天", 1),
	//按日历的周为分界
	PER_WEEK("每周", 7),
	PER_MONTH("每月", 30),
	NEWER_DAY7("新手七天", -7),
	//起始日算起7天,起始日算1天
	TIME_OUT_WEEK("7天过期",107),
	//起始日算起8天,起始日算1天
	TIME_OUT_EIGHT("8天过期",108),
	//起始日算起9天,起始日算1天
	TIME_OUT_NINE("9天过期",109),
	//起始日算起10天,起始日算1天
	TIME_OUT_TEN("10天过期",110),
	//起始日算起11天,起始日算1天
	TIME_OUT_ELEVEN("11天过期",111),
	LIMIT_TIME("限时", 190101),
	LIMIT_ACTIVITY_TIME("限时活动", 12000),
	LIMIT_TIME_BY_WAR_TOKEN_ACTIVITY("战令活动限时", 13000);

	private String name;
	private int value;

	public static MallPeroidEnum fromValue(int value) {
		for (MallPeroidEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}