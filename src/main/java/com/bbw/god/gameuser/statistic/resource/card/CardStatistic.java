package com.bbw.god.gameuser.statistic.resource.card;

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
 * @description 卡牌统计
 * @date 2020/4/20 10:11
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CardStatistic extends ResourceStatistic {
    private Integer goldCardNum = 0;
    private Integer woodCardNum = 0;
    private Integer waterCardNum = 0;
    private Integer fireCardNum = 0;
    private Integer earthCardNum = 0;
    /**
     * 五星卡统计，包括旧卡
     */
    private Map<WayEnum, Integer> fiveStarWayMap = new HashMap<>(16);
    private Map<WayEnum, Integer> todayWayMap = new HashMap<>(16);
    private Map<WayEnum, Integer> totalWayMap = new HashMap<>(16);

    public CardStatistic(Integer today, Integer total, Integer date, int type, Integer goldCardNum,
                         Integer woodCardNum, Integer waterCardNum, Integer fireCardNum, Integer earthCardNum,
                         Map<WayEnum, Integer> fiveStarWayMap, Map<WayEnum, Integer> todayWayMap,
                         Map<WayEnum, Integer> totalWayMap) {
        super(today, total, date, AwardEnum.KP, type);
        this.goldCardNum = goldCardNum;
        this.woodCardNum = woodCardNum;
        this.waterCardNum = waterCardNum;
        this.fireCardNum = fireCardNum;
        this.earthCardNum = earthCardNum;
        this.fiveStarWayMap = fiveStarWayMap;
        this.todayWayMap = todayWayMap;
        this.totalWayMap = totalWayMap;
    }

    public void addCards(int gold, int wood, int water, int fire, int earth, int fiveStar, int gainNum, WayEnum way) {
        int addTotal = gold + wood + water + fire + earth;
        this.setToday(this.getToday() + addTotal);
        this.setTotal(this.getTotal() + addTotal);
        this.setGoldCardNum(this.getGoldCardNum() + gold);
        this.setWoodCardNum(this.getWoodCardNum() + wood);
        this.setWaterCardNum(this.getWaterCardNum() + water);
        this.setFireCardNum(this.getFireCardNum() + fire);
        this.setEarthCardNum(this.getEarthCardNum() + earth);
        int value = this.fiveStarWayMap.get(way) == null ? 0 : this.fiveStarWayMap.get(way);
        this.fiveStarWayMap.put(way, value + fiveStar);
        int todayGain = this.todayWayMap.get(way) == null ? 0 : this.todayWayMap.get(way);
        this.todayWayMap.put(way, todayGain + gainNum);
        int totalGain = this.totalWayMap.get(way) == null ? 0 : this.totalWayMap.get(way);
        this.totalWayMap.put(way, totalGain + gainNum);
    }

	public CardStatistic(int type) {
		super(AwardEnum.KP, type);
	}
}
