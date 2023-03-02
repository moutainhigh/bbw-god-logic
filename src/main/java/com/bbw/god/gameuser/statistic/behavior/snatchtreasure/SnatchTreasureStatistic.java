package com.bbw.god.gameuser.statistic.behavior.snatchtreasure;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 夺宝统计
 * @date 2020/6/30 14:36
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SnatchTreasureStatistic extends BehaviorStatistic {
	// 本周抽奖次数
	private Integer weekDrawTimes = 0;

	public SnatchTreasureStatistic(Integer weekDrawTimes) {
		super(BehaviorType.SNATCH_TREASURE_DRAW);
		this.weekDrawTimes = weekDrawTimes;
	}

	public SnatchTreasureStatistic(Integer today, Integer total, Integer date, Integer weekDrawTimes) {
		super(today, total, date, BehaviorType.SNATCH_TREASURE_DRAW);
		this.weekDrawTimes = weekDrawTimes;
	}
}
