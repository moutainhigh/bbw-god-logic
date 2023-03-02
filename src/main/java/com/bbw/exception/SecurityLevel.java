package com.bbw.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 安全等级
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-12 16:45
 */
@Getter
@AllArgsConstructor
public enum SecurityLevel implements Serializable {

	HIGH("高", 4), NORMAL("一般", 2), LOW("低", 1);

	private String name;
	private int value;

	public static SecurityLevel fromValue(int value) {
		for (SecurityLevel model : values()) {
			if (model.getValue() == value) {
				return model;
			}
		}
		return null;
	}
}
