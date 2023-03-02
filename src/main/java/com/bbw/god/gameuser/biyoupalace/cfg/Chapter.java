package com.bbw.god.gameuser.biyoupalace.cfg;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 碧游宫篇章
 * 
 * @author suhq
 * @date 2019-09-11 15:08:58
 */
@Getter
@AllArgsConstructor
public enum Chapter implements Serializable {
	One("一篇", 1),
	Two("二篇", 2),
	Three("三篇", 3),
	Four("四篇", 4),
	Five("五篇", 5),
	SB1("秘传1", 98),
	SB2("秘传2", 99);

	private final String name;
	private final int value;

	public static Chapter fromValue(int value) {
		for (Chapter item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}

		return null;
	}

	public static Chapter fromName(String name) {
		for (Chapter item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}

		return null;
	}
}