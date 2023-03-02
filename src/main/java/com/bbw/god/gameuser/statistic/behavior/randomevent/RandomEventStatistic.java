package com.bbw.god.gameuser.statistic.behavior.randomevent;

import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suchaobin
 * @description 随机事件统计
 * @date 2020/4/22 16:00
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RandomEventStatistic extends BehaviorStatistic {
	private Map<YdEventEnum, Integer> todayMap = new HashMap<>(16);
	private Map<YdEventEnum, Integer> totalMap = new HashMap<>(16);
	private Integer continuousDebuff = 0;

	public RandomEventStatistic() {
		super(BehaviorType.RANDOM_EVENT);
	}

	public RandomEventStatistic(Integer today, Integer total, Integer date, Map<YdEventEnum, Integer> todayMap,
								Map<YdEventEnum, Integer> totalMap, Integer continuousDebuff) {
		super(today, total, date, BehaviorType.RANDOM_EVENT);
		this.todayMap = todayMap;
		this.totalMap = totalMap;
		this.continuousDebuff = continuousDebuff;
	}
}
