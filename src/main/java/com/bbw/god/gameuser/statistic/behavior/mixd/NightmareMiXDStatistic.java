package com.bbw.god.gameuser.statistic.behavior.mixd;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author lzc
 * @description 梦魇迷仙洞统计
 * @date 2021/06/04 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NightmareMiXDStatistic extends BehaviorStatistic {
	private Integer beatPatrol = 0;//击败巡使数量
	private Integer drinkWater = 0;//饮用泉水数量
	private Integer stepTrap = 0;//踩到陷阱次数
	private Integer openSpecialBox = 0;//巡使驻地中打开特殊宝箱
	private Integer beatDefier = 0;//作为层主击败挑战者
	private Integer passOfOneHP = 0;//1点生命值状态通过一层
	private Integer biteTheDust = 0;//作为层主没有击败挑战者被取代
	private Integer drinkWaterToEleven = 0;//饮用泉水后生命值超过10点（即11点）
	private Integer beatPatrolBoss = 0;//击败巡使头领
	private Integer continuousSmeltFail = 0;//连续熔炼失败次数
	private Integer fullLifePass = 0;//不损失生命值通过一层

	public NightmareMiXDStatistic() {
		super(BehaviorType.NIGHTMARE_MI_XD);
	}

	public NightmareMiXDStatistic(Integer beatPatrol, Integer drinkWater, Integer stepTrap,
                                  Integer openSpecialBox,Integer beatDefier,Integer passOfOneHP,
								  Integer biteTheDust,Integer drinkWaterToEleven,Integer beatPatrolBoss,
								  Integer continuousSmeltFail,Integer fullLifePass) {
		super(BehaviorType.NIGHTMARE_MI_XD);
		this.beatPatrol = beatPatrol;
		this.drinkWater = drinkWater;
		this.stepTrap = stepTrap;
		this.openSpecialBox = openSpecialBox;
		this.beatDefier = beatDefier;
		this.passOfOneHP = passOfOneHP;
		this.biteTheDust = biteTheDust;
		this.drinkWaterToEleven = drinkWaterToEleven;
		this.beatPatrolBoss = beatPatrolBoss;
		this.continuousSmeltFail = continuousSmeltFail;
		this.fullLifePass = fullLifePass;
	}
}
