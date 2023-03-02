package com.bbw.god.game.combat.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum CombatBuffEnum implements Serializable {
	TAI_TI_FU("泰体符", 131010),
	DING_FENG_FU("定风符", 131020),
	TIAN_CAN_FU("天残符", 131030),
	ZHAO_HUN_FU("招魂", 131040),
	WU_QI_FU("舞戚符", 131050),
	FEI_TIAN_FU("飞天符", 131060),
	JI_CHI_FU("疾驰符", 131070),
	SHEN_JIAN_FU("神剑符", 131080),
	ZI_YU_FU("自愈符", 131090),
	JIN_FA_FU("禁法符", 131100),
	WU_XIANG_FU("无相符", 131110),
	FEI_XIAN_FU("飞仙符", 131120),
	SHENG_HUO_FU("圣火符", 131130),
	CHUANG_CI_FU("穿刺符", 131140),
	GU_HUO_FU("蛊惑符", 131150),
	MIE_PO_FU("灭魄符", 131160),
	DU_ZHAO_FU("毒沼符", 131170),
	FENG_TIAN_FU("封天符", 131180),
	KE_HUO_FU("克火符", 131190),
	TIAO_LI_FU("挑离符", 131200);

	private String name;
	private int value;

	public static CombatBuffEnum fromValue(int value) {
		for (CombatBuffEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}