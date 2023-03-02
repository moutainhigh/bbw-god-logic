package com.bbw.god.game.dfdj.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 战区
 * 
 * @author suhq
 * @date 2019-06-21 11:33:21
 */
@Getter
@AllArgsConstructor
public enum ZoneType {

	ZONE_ONE("战区一", 10),
	ZONE_TWO("战区二", 20),
	ZONE_THREE("战区三", 30);

	private final String name;
	private final int value;

	public static ZoneType fromValue(int value) {
		for (ZoneType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
