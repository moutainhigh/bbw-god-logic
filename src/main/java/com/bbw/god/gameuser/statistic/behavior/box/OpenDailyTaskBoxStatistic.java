package com.bbw.god.gameuser.statistic.behavior.box;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 开启每日任务宝箱
 * @date 2020/4/21 11:37
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OpenDailyTaskBoxStatistic extends BehaviorStatistic {
	private Integer openAllTodayBox = 0;

	public OpenDailyTaskBoxStatistic() {
		super(BehaviorType.OPEN_DAILY_TASK_BOX);
	}

	public OpenDailyTaskBoxStatistic(Integer openAllTodayBox) {
		super(BehaviorType.OPEN_DAILY_TASK_BOX);
		this.openAllTodayBox = openAllTodayBox;
	}

	public OpenDailyTaskBoxStatistic(Integer today, Integer total, Integer date, Integer openAllTodayBox) {
		super(today, total, date, BehaviorType.OPEN_DAILY_TASK_BOX);
		this.openAllTodayBox = openAllTodayBox;
	}
}
