package com.bbw.god.gameuser.statistic;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 统计类型
 * @date 2020/4/29 10:05
 */
@Getter
@AllArgsConstructor
public enum StatisticTypeEnum {
	NONE("无", -1),
	GAIN("获得", 0),
	CONSUME("消耗", 1);
	private final String name;
	private final Integer value;

	public static StatisticTypeEnum fromValue(Integer value) {
		StatisticTypeEnum[] values = values();
		for (StatisticTypeEnum typeEnum : values) {
			if (typeEnum.getValue().equals(value)) {
				return typeEnum;
			}
		}
		throw new CoderException("没有value=" + value + "的枚举");
	}
}
