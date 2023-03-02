package com.bbw.god.game.sxdh.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机器人类型，依次优先级降低
 * 
 * @author suhq
 * @date 2019-07-15 17:11:02
 */
@Getter
@AllArgsConstructor
public enum SxdhRoboterType {
	REAL("真人", 0),
	ONE("同一阶段头衔匹配的玩家", 10),
	TWO("称号换等级匹配玩家", 20),
	THREE("按自身卡牌随机", 30),
	FOUR("按自身卡牌", 40);

	private String name;
	private int value;

	public static SxdhRoboterType fromValue(int value) {
		for (SxdhRoboterType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
