package com.bbw.god.gameuser.statistic.behavior.building;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 领取城内建筑物产出奖励统计
 * @date 2020/11/24 16:59
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BuildingAwardStatistic extends BehaviorStatistic {
	// 领取总次数
	private Integer jxzTotal = 0;
	private Integer qzTotal = 0;
	private Integer kcTotal = 0;
	private Integer lblTotal = 0;
	private Integer ldfTotal = 0;

	// 今日领取次数
	private Integer jxzToday = 0;
	private Integer qzToday = 0;
	private Integer kcToday = 0;
	private Integer lblToday = 0;
	private Integer ldfToday = 0;


	public BuildingAwardStatistic() {
		super(BehaviorType.BUILDING_AWARD);
	}

	public BuildingAwardStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.BUILDING_AWARD);
	}

	public BuildingAwardStatistic(Integer today, Integer total, Integer date, Integer jxzTotal, Integer qzTotal,
								  Integer kcTotal, Integer lblTotal, Integer ldfTotal, Integer jxzToday, Integer qzToday,
								  Integer kcToday, Integer lblToday, Integer ldfToday) {
		super(today, total, date, BehaviorType.BUILDING_AWARD);
		this.jxzTotal = jxzTotal;
		this.qzTotal = qzTotal;
		this.kcTotal = kcTotal;
		this.lblTotal = lblTotal;
		this.ldfTotal = ldfTotal;

		this.jxzToday = jxzToday;
		this.qzToday = qzToday;
		this.kcToday = kcToday;
		this.lblToday = lblToday;
		this.ldfToday = ldfToday;
	}
}
