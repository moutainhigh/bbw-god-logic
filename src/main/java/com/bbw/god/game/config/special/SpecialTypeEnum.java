package com.bbw.god.game.config.special;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 特产类型
 *
 * @author suhq
 * @date 2019年3月25日 下午4:02:54
 */
@Getter
@AllArgsConstructor
public enum SpecialTypeEnum implements Serializable {
	NORMAL("普通特产", 10),
	HIGH("高级特产", 20),
	TOP("顶级特产", 30),
	SYNTHETIC("合成特产", 40),
	SPECIAL("特殊特产", 45),
	;

	private String name;
	private int value;

	public static SpecialTypeEnum fromValue(int value) {
		for (SpecialTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
