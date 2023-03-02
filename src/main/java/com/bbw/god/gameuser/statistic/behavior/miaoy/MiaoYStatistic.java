package com.bbw.god.gameuser.statistic.behavior.miaoy;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 文王庙统计
 * @date 2020/4/23 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MiaoYStatistic extends BehaviorStatistic {
	private Integer upUp = 0;
	private Integer up = 0;
	private Integer middle = 0;
	private Integer down = 0;

	public MiaoYStatistic() {
		super(BehaviorType.WWM_DRAW);
	}

	public MiaoYStatistic(Integer today, Integer total, Integer date, Integer upUp, Integer up, Integer middle,
						  Integer down) {
		super(today, total, date, BehaviorType.WWM_DRAW);
		this.upUp = upUp;
		this.up = up;
		this.middle = middle;
		this.down = down;
	}
}
