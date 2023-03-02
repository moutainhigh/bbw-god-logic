package com.bbw.god.gameuser.statistic.behavior.recharge;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 充值统计
 * @date 2020/7/3 15:06
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RechargeStatistic extends BehaviorStatistic {
	public RechargeStatistic() {
		super(BehaviorType.RECHARGE);
	}

	public RechargeStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.RECHARGE);
	}
}
