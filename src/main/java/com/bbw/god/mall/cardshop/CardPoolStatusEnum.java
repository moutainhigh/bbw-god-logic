package com.bbw.god.mall.cardshop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卡池状态
 * 
 * @author suhq
 * @date 2019-05-14 09:38:20
 */
@Getter
@AllArgsConstructor
public enum CardPoolStatusEnum {
	LOCK("未解锁", 0),
	UNLOCK("解锁", 1);

	private String name;
	private int value;

	public static CardPoolStatusEnum fromValue(int value) {
		for (CardPoolStatusEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
