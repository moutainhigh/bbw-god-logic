package com.bbw.god.mall.snatchtreasure;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 夺宝箱子状态
 * @date 2020/6/30 10:20
 **/
@Getter
@AllArgsConstructor
public enum SnatchTreasureBoxStatus {
	NO_ACCOMPLISHED("未达成", 0),
	ACCOMPLISHED("已达成", 1),
	AWARDED("已领取", 2);

	private final String name;
	private final Integer value;

	public static SnatchTreasureBoxStatus fromValue(int value) {
		for (SnatchTreasureBoxStatus item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
