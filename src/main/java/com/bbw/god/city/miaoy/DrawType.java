package com.bbw.god.city.miaoy;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 下午4:01:12
 *
 */
@Getter
@AllArgsConstructor
public enum DrawType implements Serializable {
	DRAW_MONEY("求财", 10),
	DRAW_TREASURE("求宝", 20),
	DRAW_CARD("求贤", 30);

	private String name;
	private int value;

	public static DrawType fromValue(int value) {
		for (DrawType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}

		return null;
	}
}