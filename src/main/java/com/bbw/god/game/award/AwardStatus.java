package com.bbw.god.game.award;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 奖励状态
 * 
 * @author suhq
 * @date 2019年3月3日 下午11:20:32
 */
@Getter
@AllArgsConstructor
public enum AwardStatus {

	LOCK("未解锁", -5),
	ENABLE_REPLENISH("可补领", -4),
	READY_REPLENISH("待补领", -3),
	TIME_OUT("已过期", -2),
	UNAWARD("不能领取", -1),
	ENABLE_AWARD("可领取", 0),
	AWARDED("已领取", 1),
	ACHIEVED("已达成", 2),
	CONTINUE_AWARD("继续领取",3)
	;

	private String name;
	private int value;

	public static AwardStatus fromValue(int value) {
		for (AwardStatus item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
