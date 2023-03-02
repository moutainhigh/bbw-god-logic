package com.bbw.god.gameuser.kunls.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 注灵位置枚举
 *
 * @author: huanghb
 * @date: 2022/9/19 16:19
 */
@Getter
@AllArgsConstructor
public enum InfusionPositionEnum implements Serializable {
	INFUSION_POSITION_ONE("注灵位置一", 1),
	INFUSION_POSITION_TWO("注灵位置二", 2),
	INFUSION_POSITION_THREE("注灵位置三", 3);
	private final String name;
	private final int value;

}
