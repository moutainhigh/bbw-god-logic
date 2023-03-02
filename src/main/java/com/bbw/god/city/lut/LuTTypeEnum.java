package com.bbw.god.city.lut;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 鹿台操作类型
 *
 * @author lzc
 */
@Getter
@AllArgsConstructor
public enum LuTTypeEnum implements Serializable {
	CARD_UP("卡牌升级", 10),
	CARD_LV_BACK("重置等级", 20),
	CARD_HV_BACK("重置阶级", 30),
	;
	private String name;
	private int value;

	public static LuTTypeEnum fromValue(int value) {
		for (LuTTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}