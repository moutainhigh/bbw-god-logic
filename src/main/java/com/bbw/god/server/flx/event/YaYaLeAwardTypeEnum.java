package com.bbw.god.server.flx.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 下注奖励枚举
 * @date 2020/3/2 16:58
 */
@Getter
@AllArgsConstructor
public enum YaYaLeAwardTypeEnum {
	FIRST_PRIZE("头奖", 10),
	ENCOURAGE("鼓励奖", 20),
	;

	private String name;
	private int value;

	public static YaYaLeAwardTypeEnum fromValue(int value) {
		for (YaYaLeAwardTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

	public static YaYaLeAwardTypeEnum fromName(String name) {
		for (YaYaLeAwardTypeEnum model : values()) {
			if (model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}
}
