package com.bbw.god.gameuser.statistic.behavior.sxdh;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @description 神仙大会统计
 * @date 2020/4/22 14:00
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SxdhStatistic extends BehaviorStatistic {
	private Integer joinDays = 0;
	private Integer todayWin = 0;
	private Integer totalWin = 0;
	private Integer continuousWin = 0;
	/**
	 * key是season，值是对应数据
	 */
	private Map<String, Integer> seasonWinMap = new HashMap<>(16);
	private Map<String, Integer> seasonJoinDaysMap = new HashMap<>(16);
	private Map<String, Integer> seasonDefeatHpMap = new HashMap<>(16);
	private Map<String, Integer> seasonKillCardsMap = new HashMap<>(16);
	private Map<String, Integer> seasonChangeCardsMap = new HashMap<>(16);
	/**
	 * 季中排名
	 */
	private Map<String, Integer> middleSeasonRankMap = new HashMap<>();
	/**
	 * 总排名
	 */
	private Map<String, Integer> seasonRankMap = new HashMap<>();

	public SxdhStatistic(Integer today, Integer total, Integer date, Integer joinDays, Integer todayWin,
						 Integer totalWin, Integer continuousWin, Map<String, Integer> seasonWinMap,
						 Map<String, Integer> seasonJoinDaysMap, Map<String, Integer> defeatHpMap,
						 Map<String, Integer> killCardsMap, Map<String, Integer> changeCardsMap,
						 Map<String, Integer> middleSeasonRankMap, Map<String, Integer> seasonRankMap) {
		super(today, total, date, BehaviorType.SXDH_FIGHT);
		this.joinDays = joinDays;
		this.todayWin = todayWin;
		this.totalWin = totalWin;
		this.continuousWin = continuousWin;
		this.seasonWinMap = seasonWinMap;
		this.seasonJoinDaysMap = seasonJoinDaysMap;
		this.seasonDefeatHpMap = defeatHpMap;
		this.seasonKillCardsMap = killCardsMap;
		this.seasonChangeCardsMap = changeCardsMap;
		this.middleSeasonRankMap = middleSeasonRankMap;
		this.seasonRankMap = seasonRankMap;
	}

	public void killCards(int killNum, String season) {
		Integer val = this.seasonKillCardsMap.get(season) == null ? 0 : this.seasonKillCardsMap.get(season);
		this.seasonKillCardsMap.put(season, val + killNum);
	}

	public void defeatHp(int defeatHp, String season) {
		Integer val = this.seasonDefeatHpMap.get(season) == null ? 0 : this.seasonDefeatHpMap.get(season);
		this.seasonDefeatHpMap.put(season, val + defeatHp);
	}

	public void changeCards(int changeCards, String season) {
		Integer val = this.seasonChangeCardsMap.get(season) == null ? 0 : this.seasonChangeCardsMap.get(season);
		this.seasonChangeCardsMap.put(season, val + changeCards);
	}

	public void addJoinDays(String season) {
		if (1 == this.getToday()) {
			this.joinDays += 1;
			int val = this.seasonJoinDaysMap.get(season) == null ? 0 : this.seasonJoinDaysMap.get(season);
			this.seasonJoinDaysMap.put(season, val + 1);
		}
	}

	public void addSeasonWin(String season) {
		Integer val = this.seasonWinMap.get(season) == null ? 0 : this.seasonWinMap.get(season);
		this.seasonWinMap.put(season, val + 1);
	}

	public SxdhStatistic() {
		super(BehaviorType.SXDH_FIGHT);
	}
}
