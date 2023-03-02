package com.bbw.god.gameuser.biyoupalace.cfg;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 碧游宫属性
 * 
 * @author suhq
 * @date 2019-09-11 15:08:58
 */
@Getter
@AllArgsConstructor
public enum ChapterType implements Serializable {
	Gold("金", 10),
	Wood("木", 20),
	Water("水", 30),
	Fire("火", 40),
	Earth("土", 50),
	SecretBiography("秘传", 60);

	private String name;
	private int value;

	public static ChapterType fromValue(int value) {
		for (ChapterType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}

		return null;
	}

	public static ChapterType fromName(String name) {
		for (ChapterType item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}

		return null;
	}
}