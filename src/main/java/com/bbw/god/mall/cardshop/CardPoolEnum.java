package com.bbw.god.mall.cardshop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物品类别
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 *
 */
@Getter
@AllArgsConstructor
public enum CardPoolEnum {
	GOLD_CP("金之卡池", 10),
	WOOD_CP("木之卡池", 20),
	WATER_CP("水之卡池", 30),
	FIRE_CP("火之卡池", 40),
	EARTH_CP("土之卡池", 50),
	WANWU_CP("万物卡池", 60),
	JUX_CP("聚贤卡池", 70),
	LIMIT_TIME_CP("限时卡池", 80),
	;

	private final String name;
	private final int value;

	public static CardPoolEnum fromValue(int value) {
		for (CardPoolEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
