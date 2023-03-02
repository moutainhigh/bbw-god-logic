package com.bbw.god.gameuser.statistic.behavior.box;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 开启行会宝箱统计
 * @date 2020/4/23 9:25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OpenGuildBoxStatistic extends BehaviorStatistic {
	public OpenGuildBoxStatistic() {
		super(BehaviorType.OPEN_GUILD_BOX);
	}

	public OpenGuildBoxStatistic(Integer today, Integer total, Integer date) {
		super(today, total, date, BehaviorType.OPEN_GUILD_BOX);
	}
}
