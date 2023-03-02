package com.bbw.god.gameuser.statistic.behavior.biyou;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 碧游宫统计
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BiYouStatistic extends BehaviorStatistic {
	/** 已解锁的秘传数 */
	private int mzSkillNum = 0;

	public BiYouStatistic() {
		super(BehaviorType.BI_YOU);
	}

	public BiYouStatistic(Integer today, Integer total, Integer date, Integer mzSkillNum) {
		super(today, total, date, BehaviorType.BI_YOU);
		this.mzSkillNum = mzSkillNum;
	}
}
