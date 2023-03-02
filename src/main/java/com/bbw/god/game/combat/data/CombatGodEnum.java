package com.bbw.god.game.combat.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 神仙 type 10 按步数 20 按时间 30 下一城战 40 一次性
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 下午3:12:19
 *
 */
@Getter
@AllArgsConstructor
public enum CombatGodEnum implements Serializable {

	XCS("小财神", 10, 10, 20), // 生效步数
	DCS("大财神", 10, 20, 20),

	TB("天兵", 30, 60, 1), // 下个城战
	TJ("天将", 30, 70, 1),

	XB("虾兵", 30, 90, 1),
	XJ("蟹将", 30, 100, 1),

	QS("穷神", 10, 130, 20);

	private String name;
	private int type;
	private int value;
	private int effect;

	public static CombatGodEnum fromValue(int value) {
		for (CombatGodEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}