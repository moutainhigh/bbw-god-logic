package com.bbw.god.fight;

import com.bbw.common.PowerRandom;

import java.util.List;

public class FightResultUtil {

	/**
	 * 根据等级获得战斗血量
	 * 
	 * @param level
	 * @return
	 */
	public static int getBloodByLevel(int level) {
		return (level - 1) * 380 + 1000;
	}

	/**
	 * 获得战斗奖励经验
	 * 
	 * @param result
	 * @return
	 */
	public static int getFightExp(long oppLostBlood, List<FightSubmitParam.SubmitCardParam> killedCards) {
		int gainExp = FightResultUtil.getExpByBeatSoliders(killedCards);
		gainExp += 300 + oppLostBlood / 10;
		// 最终数值需乘上0.9~1.1的随机修正值
		gainExp *= (PowerRandom.getRandomBySeed(21) + 89) / 100.0;
		return gainExp;
	}
	private static int getExpByBeatSoliders(List<FightSubmitParam.SubmitCardParam> killedCards) {
		int gainExp = 0;
		for (FightSubmitParam.SubmitCardParam cardParam:killedCards){
			gainExp += (int) (getStarAsExpFactor(cardParam.getStar()) * (1 + cardParam.getLv() * (1 +  cardParam.getHv() * 0.5) / 10.0));
		}
		return gainExp;
	}
	/**
	 * 获得卡牌星级战斗经验因子
	 * 
	 * @param star
	 * @return
	 */
	private static int getStarAsExpFactor(int star) {
		switch (star) {
		case 1:
			return 10;
		case 2:
			return 20;
		case 3:
			return 40;
		case 4:
			return 100;
		case 5:
			return 200;
		default:
			return 0;
		}
	}

}
