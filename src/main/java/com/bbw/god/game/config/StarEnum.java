package com.bbw.god.game.config;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 星级
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 下午4:01:35
 *
 */
@Getter
@AllArgsConstructor
public enum StarEnum implements Serializable {

	One("一星", 1),
	Two("二星", 2),
	Three("三星", 3),
	Four("四星", 4),
	Five("五星", 5);

	private String name;
	private int value;

	public static StarEnum fromValue(int value) {

		for (StarEnum model : values()) {
			if (model.getValue() == value) {
				return model;
			}
		}
		return null;
	}

	public static StarEnum fromName(String name) {

		for (StarEnum model : values()) {
			if (model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}
}
