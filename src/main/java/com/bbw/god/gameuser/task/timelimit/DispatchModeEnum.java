package com.bbw.god.gameuser.task.timelimit;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 派遣模式
 *
 * @author: suhq
 * @date: 2022/12/9 11:13 上午
 */
@Getter
@AllArgsConstructor
public enum DispatchModeEnum implements Serializable {
	SUCCESS_RATE_MODE("派遣卡牌影响成功率(默认)", 10),
	DISPATCH_TIME_MODE("派遣卡牌影响派遣时间", 20);

	private final String name;
	private final int value;

	public static DispatchModeEnum fromValue(int value) {

		for (DispatchModeEnum model : values()) {
			if (model.getValue() == value) {
				return model;
			}
		}
		throw new CoderException("找不到对应的枚举");
	}

	public static DispatchModeEnum fromName(String name) {

		for (DispatchModeEnum model : values()) {
			if (model.getName().equals(name)) {
				return model;
			}
		}
		throw new CoderException("找不到对应的枚举");
	}
}
