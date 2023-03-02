package com.bbw.god.gameuser.statistic.resource.special;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suchaobin
 * @description 特产统计
 * @date 2020/4/20 16:11
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SpecialStatistic extends ResourceStatistic {
	private Map<WayEnum, Integer> todayMap = new HashMap<>(16);
	private Map<WayEnum, Integer> totalMap = new HashMap<>(16);

	public SpecialStatistic(Integer today, Integer total, Integer date, int type, Map<WayEnum,
			Integer> todayMap, Map<WayEnum, Integer> totalMap) {
		super(today, total, date, AwardEnum.TC, type);
		this.todayMap = todayMap;
		this.totalMap = totalMap;
	}

	public SpecialStatistic(int type) {
		super(AwardEnum.TC, type);
	}

	public void increment(int addValue, WayEnum way) {
		this.setTotal(this.getTotal() + addValue);
		this.setToday(this.getToday() + addValue);
		Integer todayWayNum = this.getTodayMap().get(way) == null ? 0 : this.getTodayMap().get(way);
		Integer totalWayNum = this.getTotalMap().get(way) == null ? 0 : this.getTotalMap().get(way);
		this.todayMap.put(way, todayWayNum + addValue);
		this.totalMap.put(way, totalWayNum + addValue);
	}
}
