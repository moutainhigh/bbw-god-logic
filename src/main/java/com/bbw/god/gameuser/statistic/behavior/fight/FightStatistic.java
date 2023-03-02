package com.bbw.god.gameuser.statistic.behavior.fight;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suchaobin
 * @description 战斗统计
 * @date 2020/4/22 9:30
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FightStatistic extends BehaviorStatistic {
	private Map<FightTypeEnum, Integer> fightMap = new HashMap<>(13);
	private Map<FightTypeEnum, Integer> winFightMap = new HashMap<>(13);

	private Map<FightTypeEnum, Integer> todayFightMap = new HashMap<>(13);
	private Map<FightTypeEnum, Integer> todayWinFightMap = new HashMap<>(13);

	public FightStatistic(Integer today, Integer total, Integer date, Map<FightTypeEnum, Integer> fightMap,
						  Map<FightTypeEnum, Integer> winFightMap, Map<FightTypeEnum, Integer> todayFightMap,
						  Map<FightTypeEnum, Integer> todayWinFightMap) {
		super(today, total, date, BehaviorType.FIGHT);
		this.fightMap = fightMap;
		this.winFightMap = winFightMap;
		this.todayFightMap = todayFightMap;
		this.todayWinFightMap = todayWinFightMap;
	}

	public FightStatistic() {
		super(BehaviorType.FIGHT);
	}

	public void increment(FightTypeEnum fightTypeEnum, boolean isWin) {
		this.setToday(this.getToday() + 1);
		this.setTotal(this.getTotal() + 1);
		int fightNum = this.fightMap.get(fightTypeEnum) == null ? 0 : this.fightMap.get(fightTypeEnum);
		this.fightMap.put(fightTypeEnum, fightNum + 1);
		int todayFightNum = this.todayFightMap.get(fightTypeEnum) == null ? 0 : this.todayFightMap.get(fightTypeEnum);
		this.todayFightMap.put(fightTypeEnum, todayFightNum + 1);
		if (isWin) {
			int winNum = this.winFightMap.get(fightTypeEnum) == null ? 0 : this.winFightMap.get(fightTypeEnum);
			this.winFightMap.put(fightTypeEnum, winNum + 1);
			int todayWinNum = this.todayWinFightMap.get(fightTypeEnum) == null ? 0 : this.todayWinFightMap.get(fightTypeEnum);
			this.todayWinFightMap.put(fightTypeEnum, todayWinNum + 1);
		}
	}
}
