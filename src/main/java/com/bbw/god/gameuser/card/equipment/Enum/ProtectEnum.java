package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 保护属性枚举
 *
 * @author: huanghb
 * @date: 2022/9/19 16:19
 */
@Getter
@AllArgsConstructor
public enum ProtectEnum implements Serializable {
	UNPROTECTED("未保护", 0),

	PROTECT("保护", 1);

	private final String name;
	private final int value;

}
