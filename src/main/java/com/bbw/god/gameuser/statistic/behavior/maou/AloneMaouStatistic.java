package com.bbw.god.gameuser.statistic.behavior.maou;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 独战魔王统计
 * @date 2020/4/22 10:31
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AloneMaouStatistic extends BehaviorStatistic {
	/**
	 * 已通关的金魔王层数
	 */
	private Integer gold = 0;
	private Integer wood = 0;
	private Integer water = 0;
	private Integer fire = 0;
	private Integer earth = 0;

	public AloneMaouStatistic() {
		super(BehaviorType.MAOU_ALONE_FIGHT);
	}

	public AloneMaouStatistic(Integer today, Integer total, Integer date, Integer gold, Integer wood, Integer water,
							  Integer fire, Integer earth) {
		super(today, total, date, BehaviorType.MAOU_ALONE_FIGHT);
		this.gold = gold;
		this.wood = wood;
		this.water = water;
		this.fire = fire;
		this.earth = earth;
	}
}
