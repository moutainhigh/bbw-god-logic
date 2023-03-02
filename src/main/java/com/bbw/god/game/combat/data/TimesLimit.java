package com.bbw.god.game.combat.data;

import com.bbw.god.game.config.card.CfgCardSkill;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 法宝使用限制
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 23:53
 */
@ToString
@NoArgsConstructor
@Data
public class TimesLimit implements Serializable {
	private static final long serialVersionUID = -6978220771790484595L;
	private int initTotalTimes = Integer.MAX_VALUE;// 一场战斗可使用的次数
	private int initRoundTimes = Integer.MAX_VALUE;// 一个回合可使用的次数
	private int initRounds = 30;// 可以使用多少回合，默认30回合

	private int currentTotalTimes = Integer.MAX_VALUE;// 一场战斗可使用的次数
	private int currentRoundTimes = Integer.MAX_VALUE;// 一个回合可使用的次数
	private int currentRounds = 30;// 可以使用多少回合，默认30回合

	private int banFrom=-1;//被什么技能禁用的

	public TimesLimit(int totalTimes, int roundTimes) {
		this.initTotalTimes = totalTimes;
		this.initRoundTimes = roundTimes;
		reset();

	}


	public static TimesLimit instance(int totalTimes, int roundTimes, int rounds) {
		TimesLimit limit=new TimesLimit();
		limit.setInitTotalTimes(totalTimes);
		limit.setInitRoundTimes(roundTimes);
		limit.setInitRounds(rounds);
		limit.reset();
		return limit;
	}

	public static TimesLimit instance(CfgCardSkill cardSkill){
		TimesLimit limit=new TimesLimit();
		limit.setInitTotalTimes(cardSkill.getTotalTimes());
		limit.setInitRoundTimes(cardSkill.getRoundTimes());
		limit.setInitRounds(cardSkill.getRound());
		limit.reset();
		return limit;
	}

	/**
	 * 初始化一个次数为1的实例
	 *
	 * @return
	 */
	public static TimesLimit oneTimeLimit() {
		return TimesLimit.instance(1,1,1);
	}

	/**
	 * 初始化一个回合数为1，回合内不限次数 的实例
	 *
	 * @return
	 */
	public static TimesLimit oneRoundLimit() {
		return TimesLimit.instance(1000, 1000,1);
	}

	/**
	 * 初始化一个回合数为30，总次数不限，回合内不限次数 的实例
	 *
	 * @return
	 */
	public static TimesLimit noLimit() {
		// TODO 最大回合数待优化
		return TimesLimit.instance(1000, 1000,60);
	}

	/**
	 * 是否是永久禁用
	 * @return
	 */
	public boolean isForbid(){
		return this.currentRounds==0 || this.currentTotalTimes==0;
	}

	/**
	 * 是否还有释放次数：
	 * </br>仅检查是否被禁用  以及 总释放次数和回合数，不考虑当前回合内是否还有次数
	 * @return
	 */
	public boolean hasPerformTimes() {
		return  this.currentRounds > 0 && this.currentTotalTimes > 0;
	}

	/**
	 * 当前回合是否可以释放
	 * </br>即必须要保证 否被禁用  以及 总释放次数、回合数、当前回合可用次数
	 * @return
	 */
	public boolean isRoundEffective(){
		return  this.currentRounds > 0 && this.currentTotalTimes > 0 && this.currentRoundTimes > 0;
	}
	/**
	 * 重置
	 */
	public void reset() {
		this.currentTotalTimes = this.initTotalTimes;
		this.currentRounds=this.initRounds;
		this.currentRoundTimes=this.initRoundTimes;
		this.banFrom=-1;
	}

	/**
	 * 回合重置
	 */
	public void roundReset() {
		if (isForbid()){
			return;
		}
		//重置 每回合可用次数
		this.currentRoundTimes = this.initRoundTimes;
		//可用回合数减1
		this.currentRounds--;
		if (this.currentRounds==0){
			return;
		}
		this.banFrom=-1;
	}
	/**
	 * 封锁1回合
	 */
	public void forbidOneRound(int banFrom) {
		this.currentRoundTimes=0;
		this.banFrom=banFrom;
	}

	/**
	 * 永久封锁: 即可用次数为0
	 */
	public void forbid() {
		this.currentTotalTimes = 0;
	}


	/**
	 * 扣除一次使用次数
	 */
	public void lostTimes() {
		this.currentTotalTimes--;
		this.currentRoundTimes--;
	}
}