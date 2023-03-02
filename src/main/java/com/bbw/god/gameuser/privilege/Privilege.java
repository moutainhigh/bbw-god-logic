package com.bbw.god.gameuser.privilege;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 特权
 * 
 * @author suhq
 * @date 2019-09-18 14:52:35
 */
@Getter
@AllArgsConstructor
public enum Privilege {

	DilingYin("地灵印", 10),
	TianlingYin("天灵印", 20);

	private String name;
	private int value;

	public static Privilege fromValue(int value) {
		for (Privilege item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
