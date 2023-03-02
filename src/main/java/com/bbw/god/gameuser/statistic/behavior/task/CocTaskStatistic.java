package com.bbw.god.gameuser.statistic.behavior.task;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 商会任务统计
 * @date 2020/4/22 9:06
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CocTaskStatistic extends BehaviorStatistic {
	public CocTaskStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.CHAMBER_OF_COMMERCE_EXP_TASK);
	}

	public CocTaskStatistic() {
		super(BehaviorType.CHAMBER_OF_COMMERCE_EXP_TASK);
	}
}