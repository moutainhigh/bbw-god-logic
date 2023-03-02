package com.bbw.god.gameuser.statistic.behavior.login;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 登录统计
 * @date 2020/4/21 14:56
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginStatistic extends BehaviorStatistic {
	private Integer loginDays = 0;

	public LoginStatistic() {
		super(BehaviorType.LOGIN);
	}

	public LoginStatistic(Integer today, Integer total, Integer date, Integer loginDays) {
		super(today, total, date, BehaviorType.LOGIN);
		this.loginDays = loginDays;
	}
}
