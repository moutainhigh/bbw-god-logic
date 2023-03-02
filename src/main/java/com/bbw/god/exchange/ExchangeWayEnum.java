package com.bbw.god.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 兑换方式
 * 
 * @author suhq
 * @date 2018年12月26日 上午9:53:12
 */
@Getter
@AllArgsConstructor
public enum ExchangeWayEnum {

	FST("封神台", 1),
	ZXZ("诛仙阵", 2),
	XJBK("星君宝库", 3),
	DHM("兑换码", 4),
	SXDH("神仙大会", 5);

	private String name;
	private int value;

	public static ExchangeWayEnum fromValue(int value) {
		for (ExchangeWayEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
