package com.bbw.god.gameuser.statistic.behavior.fight.fightdetail;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 战斗相关细节统计
 * @author lzc
 * @description
 * @date 2021/4/25 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FightDetailStatistic extends BehaviorStatistic {
	/** 梦魇主城1,分身卡牌击杀卡牌数量 */
	private int nightmareMainCity4325LeaderKillNum = 0;
	/** 梦魇主城2,分身卡牌击杀卡牌数量 */
	private int nightmareMainCity2539LeaderKillNum = 0;
	/** 梦魇主城3,分身卡牌击杀卡牌数量 */
	private int nightmareMainCity1024LeaderKillNum = 0;
	/** 梦魇主城4,分身卡牌击杀卡牌数量 */
	private int nightmareMainCity2608LeaderKillNum = 0;
	/** 梦魇主城5,分身卡牌击杀卡牌数量 */
	private int nightmareMainCity2725LeaderKillNum = 0;
	/** 使用麒麟击败召唤师 */
	private int qiLinBeat = 0;

	public FightDetailStatistic() {
		super(BehaviorType.FIGHT_DETAIL);
	}

	public FightDetailStatistic(Integer city4325KillNum,Integer city2539KillNum,Integer city1024KillNum,Integer city2608KillNum,Integer city2725KillNum,Integer qiLinBeat) {
		super(BehaviorType.FIGHT_DETAIL);
		this.nightmareMainCity4325LeaderKillNum = city4325KillNum;
		this.nightmareMainCity2539LeaderKillNum = city2539KillNum;
		this.nightmareMainCity1024LeaderKillNum = city1024KillNum;
		this.nightmareMainCity2608LeaderKillNum = city2608KillNum;
		this.nightmareMainCity2725LeaderKillNum = city2725KillNum;
		this.qiLinBeat = qiLinBeat;
	}
}
