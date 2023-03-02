package com.bbw.god.gameuser.statistic.behavior.move;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 移动统计
 * @date 2020/4/22 15:43
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MoveStatistic extends BehaviorStatistic {
	public MoveStatistic() {
		super(BehaviorType.MOVE);
	}

	public MoveStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.MOVE);
	}
}
