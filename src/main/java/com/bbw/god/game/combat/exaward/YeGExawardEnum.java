package com.bbw.god.game.combat.exaward;

import com.bbw.common.PowerRandom;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月27日 上午10:22:35 
* 类说明 
*/
@Getter
@AllArgsConstructor
public enum YeGExawardEnum {
	WIN_NOT_LOSE_BLOOD(1,"己方不损血赢得战斗"),
	WIN_6_ROUND(2,"6回合内赢得战斗"),
	WIN_KILLED_ALL_CARDS(3,"打掉对方的所有卡牌"),
	WIN_LOSE_3_CARD(4,"胜利时我方坟场卡牌少于3张"),
	WIN_NO_CARD_BEFORE_2_ROUND(5,"前2回合不放置卡牌并赢得战斗"),
	WIN_ELITE(6,"击败精英怪物，高级宝箱奖励");
	
	private int val;
	private String memo;

	public static YeGExawardEnum randomNormal() {
		int val = PowerRandom.getRandomBySeed(5);
		for (YeGExawardEnum tEnum : values()) {
			if (tEnum.getVal() == val) {
				return tEnum;
			}
		}
		return WIN_NOT_LOSE_BLOOD;
	}
}
