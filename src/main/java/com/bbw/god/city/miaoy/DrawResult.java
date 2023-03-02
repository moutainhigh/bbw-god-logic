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
public enum DrawResult implements Serializable {
	UP_UP("上上签", 1),
	UP("上签", 2),
	MIDDLE("中签", 3),
	DOWN("下签", 4);

	private String name;
	private int value;

	public static DrawResult fromValue(int value) {
		for (DrawResult item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}

		return null;
	}
}