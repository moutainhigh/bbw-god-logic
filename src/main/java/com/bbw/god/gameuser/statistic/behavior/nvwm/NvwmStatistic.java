package com.bbw.god.gameuser.statistic.behavior.nvwm;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 女娲庙捐赠统计
 * @date 2020/4/23 8:59
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NvwmStatistic extends BehaviorStatistic {
	private Integer todayFavorite = 0;
	private Integer totalFavorite = 0;

	public NvwmStatistic() {
		super(BehaviorType.NVWM_DONATE);
	}

	public NvwmStatistic(Integer today, Integer total, Integer date, Integer todayFavorite, Integer totalFavorite) {
		super(today, total, date, BehaviorType.NVWM_DONATE);
		this.todayFavorite = todayFavorite;
		this.totalFavorite = totalFavorite;
	}
}
