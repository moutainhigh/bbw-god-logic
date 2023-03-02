package com.bbw.god.game.sxdh.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 段位
 * 
 * @author suhq
 * @date 2019-07-16 09:25:38
 */
@Getter
@AllArgsConstructor
public enum Segment {

	INIT("初始段位", 5),
	ONE("段位1", 10),
	TWO("段位2", 20),
	THREE("段位3", 30),
	FOUR("段位4", 40),
	SPECIAL("特殊段位", 45),
	FIVE("段位5", 50),
	SIX("段位6", 60),
	SEVEN("段位7", 70);

	private String name;
	private int value;

	public static Segment fromValue(int value) {
		for (Segment item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
