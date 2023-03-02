package com.bbw.god.gameuser.statistic.behavior;

import com.bbw.god.gameuser.statistic.BaseStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 行为统计类
 * @date 2020/3/30 15:44
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BehaviorStatistic extends BaseStatistic {
	private BehaviorType behaviorType;

	public BehaviorStatistic(BehaviorType behaviorType) {
		this.behaviorType = behaviorType;
	}

	public BehaviorStatistic(Integer today, Integer total, Integer date, BehaviorType behaviorType) {
		super(today, total, date);
		this.behaviorType = behaviorType;
	}
}
