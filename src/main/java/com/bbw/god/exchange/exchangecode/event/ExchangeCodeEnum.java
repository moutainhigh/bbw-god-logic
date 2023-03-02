package com.bbw.god.exchange.exchangecode.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 兑换礼包枚举(value必须对应godmanager.packs中的对应type!!!)
 * @date 2020/3/10 9:34
 */
@Getter
@AllArgsConstructor
public enum ExchangeCodeEnum {
	WE_CHAT_WEEKLY_BAG("微信每周礼包", 30);

	private String name;
	private Integer value;

	public static ExchangeCodeEnum fromName(String name) {
		ExchangeCodeEnum[] values = ExchangeCodeEnum.values();
		for (ExchangeCodeEnum exchangeCodeEnum : values) {
			if (exchangeCodeEnum.getName().equals(name)) {
				return exchangeCodeEnum;
			}
		}
		return null;
	}

	public static ExchangeCodeEnum fromValue(Integer value) {
		ExchangeCodeEnum[] values = ExchangeCodeEnum.values();
		for (ExchangeCodeEnum exchangeCodeEnum : values) {
			if (exchangeCodeEnum.getValue().equals(value)) {
				return exchangeCodeEnum;
			}
		}
		return null;
	}
}
