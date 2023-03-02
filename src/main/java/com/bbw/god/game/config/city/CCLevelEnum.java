package com.bbw.god.game.config.city;

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
public enum CCLevelEnum implements Serializable {

	One("一级城", 1),
	Two("二级城", 2),
	Three("三级城", 3),
	Four("四级城", 4),
	Five("五级城", 5);

	private String name;
	private int value;

	public static CCLevelEnum fromValue(int value) {

		for (CCLevelEnum model : values()) {
			if (model.getValue() == value) {
				return model;
			}
		}
		return null;
	}
}
