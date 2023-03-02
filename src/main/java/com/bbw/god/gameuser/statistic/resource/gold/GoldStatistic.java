package com.bbw.god.gameuser.statistic.resource.gold;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
/**
 * 元宝统计类
 *
 * @author suchaobin
 * @date 2020/4/16 9:38
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GoldStatistic extends ResourceStatistic {
	private Map<WayEnum, Integer> todayMap = new HashMap<>(16);
	private Map<WayEnum, Integer> totalMap = new HashMap<>(16);

	public GoldStatistic(Integer today, Integer total, Integer date, int type, Map<WayEnum, Integer> todayMap,
						 Map<WayEnum, Integer> totalMap) {
		super(today, total, date, AwardEnum.YB, type);
		this.todayMap = todayMap;
		this.totalMap = totalMap;
	}

	public GoldStatistic(int type) {
		super(AwardEnum.YB, type);
	}
}
