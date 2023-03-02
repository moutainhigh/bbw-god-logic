package com.bbw.god.mall.lottery;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 奖励等级
 * @date 2020/7/6 15:46
 **/
@Getter
@AllArgsConstructor
public enum LotteryLevel {
	FIRST("一等奖", 1),
	SECOND("二等奖", 2),
	THIRD("三等奖", 3),
	FOURTH("四等奖", 4),
	FIFTH("五等奖", 5),
	PARTICIPATE("参与奖", 6),
	;

	public static LotteryLevel fromValue(int level) {
		for (LotteryLevel item : values()) {
			if (item.getLevel() == level) {
				return item;
			}
		}
		return null;
	}

	private final String name;
	private final Integer level;
}
