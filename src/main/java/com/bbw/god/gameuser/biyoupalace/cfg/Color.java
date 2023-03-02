package com.bbw.god.gameuser.biyoupalace.cfg;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 碧游宫篇章
 * 
 * @author suhq
 * @date 2019-09-11 15:08:58
 */
@Getter
@AllArgsConstructor
public enum Color implements Serializable {
	Gray("灰", 1),
	Green("绿", 10),
	Blue("蓝", 20),
	Purple("紫", 30),
	Orange("橙", 40),
	Gold("金", 50),
	Red("红", 60);

	private String name;
	private int value;

	public static Color fromValue(int value) {
		for (Color item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}

		return null;
	}

	public static Color fromName(String name) {
		for (Color item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}

		return null;
	}
}