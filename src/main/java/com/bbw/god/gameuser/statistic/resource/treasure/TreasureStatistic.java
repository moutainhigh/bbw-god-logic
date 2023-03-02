package com.bbw.god.gameuser.statistic.resource.treasure;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author suchaobin
 * @description 法宝统计
 * @date 2020/4/16 16:30
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TreasureStatistic extends ResourceStatistic {
	/**
	 * key是法宝名
	 */
	private Map<String, Map<WayEnum, Integer>> todayMap = new HashMap<>();
	private Map<String, Map<WayEnum, Integer>> totalMap = new HashMap<>();

	public TreasureStatistic(Integer today, Integer total, Integer date, int type,
							 Map<String, Map<WayEnum, Integer>> todayMap,
							 Map<String, Map<WayEnum, Integer>> totalMap) {
		super(today, total, date, AwardEnum.FB, type);
		this.todayMap = todayMap;
		this.totalMap = totalMap;
	}

	public TreasureStatistic(int type) {
		super(AwardEnum.FB, type);
	}

	public void increment(CfgTreasureEntity treasure, WayEnum way, int num) {
		this.setToday(this.getToday() + num);
		this.setTotal(this.getTotal() + num);
		Map<WayEnum, Integer> todayWayMap = this.getTodayMap().get(treasure.getName());
		if (null == todayWayMap || todayWayMap.size() == 0) {
			todayWayMap = new HashMap<>();
		}
		Integer todayWayNum = todayWayMap.get(way) == null ? 0 : todayWayMap.get(way);
		todayWayMap.put(way, todayWayNum + num);
		this.todayMap.put(treasure.getName(), todayWayMap);
		Map<WayEnum, Integer> totalWayMap = this.getTotalMap().get(treasure.getName());
		if (null == totalWayMap || totalWayMap.size() == 0) {
			totalWayMap = new HashMap<>();
		}
		Integer totalWayNum = totalWayMap.get(way) == null ? 0 : totalWayMap.get(way);
		totalWayMap.put(way, totalWayNum + num);
		this.totalMap.put(treasure.getName(), totalWayMap);
	}

	public int getTotalNum(CfgTreasureEntity treasure) {
		Map<WayEnum, Integer> totalWayMap = this.getTotalMap().get(treasure.getName());
		if (null == totalWayMap || 0 == totalWayMap.size()) {
			return 0;
		}
		Set<WayEnum> keySet = totalWayMap.keySet();
		int sum = 0;
		for (WayEnum way : keySet) {
			sum += totalWayMap.get(way);
		}
		return sum;
    }

    public int getTotalNum(CfgTreasureEntity treasure, WayEnum way) {
        Map<WayEnum, Integer> totalWayMap = this.getTotalMap().get(treasure.getName());
        if (null == totalWayMap || 0 == totalWayMap.size()) {
            return 0;
        }
        Integer num = totalWayMap.get(way);
        return null == num ? 0 : num;
    }

    public int getTodayNum(CfgTreasureEntity treasure) {
        Map<WayEnum, Integer> TodayWayMap = this.getTodayMap().get(treasure.getName());
        if (null == TodayWayMap || 0 == TodayWayMap.size()) {
            return 0;
        }
        Set<WayEnum> keySet = TodayWayMap.keySet();
        int sum = 0;
        for (WayEnum way : keySet) {
            sum += TodayWayMap.get(way);
        }
        return sum;
    }

    public int getTodayNum(CfgTreasureEntity treasure, WayEnum way) {
        Map<WayEnum, Integer> TodayWayMap = this.getTodayMap().get(treasure.getName());
        if (null == TodayWayMap || 0 == TodayWayMap.size()) {
            return 0;
        }
        Integer num = TodayWayMap.get(way);
        return null == num ? 0 : num;
    }
}
