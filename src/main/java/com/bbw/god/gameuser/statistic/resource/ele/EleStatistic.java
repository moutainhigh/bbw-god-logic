package com.bbw.god.gameuser.statistic.resource.ele;

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
 * @description 元素统计
 * @date 2020/4/20 11:05
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EleStatistic extends ResourceStatistic {
    private Integer goldEle = 0;
    private Integer woodEle = 0;
    private Integer waterEle = 0;
    private Integer fireEle = 0;
    private Integer earthEle = 0;
    private Map<WayEnum, Integer> todayMap = new HashMap<>(16);
    private Map<WayEnum, Integer> totalMap = new HashMap<>(16);
    private Map<WayEnum, Integer> goldMap = new HashMap<>(16);
    private Map<WayEnum, Integer> woodMap = new HashMap<>(16);
    private Map<WayEnum, Integer> waterMap = new HashMap<>(16);
    private Map<WayEnum, Integer> fireMap = new HashMap<>(16);
    private Map<WayEnum, Integer> earthMap = new HashMap<>(16);

    public EleStatistic(Integer today, Integer total, Integer date, int type, Integer goldEle, Integer woodEle,
                        Integer waterEle, Integer fireEle, Integer earthEle) {
        super(today, total, date, AwardEnum.YS, type);
		this.goldEle = goldEle;
		this.woodEle = woodEle;
		this.waterEle = waterEle;
		this.fireEle = fireEle;
		this.earthEle = earthEle;
	}

    public EleStatistic(Integer today, Integer total, Integer date, int type, Integer goldEle, Integer woodEle,
                        Integer waterEle, Integer fireEle, Integer earthEle, Map<WayEnum, Integer> totalMap,
                        Map<WayEnum, Integer> todayMap, Map<WayEnum, Integer> goldMap, Map<WayEnum, Integer> woodMap,
                        Map<WayEnum, Integer> waterMap, Map<WayEnum, Integer> fireMap, Map<WayEnum, Integer> earthMap) {
        super(today, total, date, AwardEnum.YS, type);
        this.goldEle = goldEle;
        this.woodEle = woodEle;
        this.waterEle = waterEle;
        this.fireEle = fireEle;
        this.earthEle = earthEle;
        this.todayMap = todayMap;
        this.totalMap = totalMap;
        this.goldMap = goldMap;
        this.woodMap = woodMap;
        this.waterMap = waterMap;
        this.fireMap = fireMap;
        this.earthMap = earthMap;
    }

	public void increment(int gold, int wood, int water, int fire, int earth, WayEnum way) {
        this.goldEle += gold;
        this.woodEle += wood;
        this.waterEle += water;
        this.fireEle += fire;
        this.earthEle += earth;
        int total = gold + wood + water + fire + earth;
        this.setToday(this.getToday() + total);
        this.setTotal(this.getTotal() + total);
        Integer wayNum = this.totalMap.get(way) == null ? 0 : this.totalMap.get(way);
        this.totalMap.put(way, wayNum + total);
        Integer todayWayNum = this.todayMap.get(way) == null ? 0 : this.todayMap.get(way);
        this.todayMap.put(way, todayWayNum + total);
        Integer goldWayNum = this.goldMap.get(way) == null ? 0 : this.goldMap.get(way);
        this.goldMap.put(way, goldWayNum + gold);
        Integer woodWayNum = this.woodMap.get(way) == null ? 0 : this.woodMap.get(way);
        this.woodMap.put(way, woodWayNum + wood);
        Integer waterWayNum = this.waterMap.get(way) == null ? 0 : this.waterMap.get(way);
        this.waterMap.put(way, waterWayNum + water);
        Integer fireWayNum = this.fireMap.get(way) == null ? 0 : this.fireMap.get(way);
        this.fireMap.put(way, fireWayNum + fire);
        Integer earthWayNum = this.earthMap.get(way) == null ? 0 : this.earthMap.get(way);
        this.earthMap.put(way, earthWayNum + earth);
	}
}
