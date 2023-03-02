package com.bbw.god.gameuser.statistic.behavior.god;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 遇到神仙统计
 * @date 2020/4/23 11:19
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GodStatistic extends BehaviorStatistic {
	public GodStatistic() {
		super(BehaviorType.MEET_GOD);
	}

	public GodStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.MEET_GOD);
	}
}
