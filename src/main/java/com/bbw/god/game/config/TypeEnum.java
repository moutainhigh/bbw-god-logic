package com.bbw.god.game.config;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 属性
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 下午3:46:21
 */
@Getter
@AllArgsConstructor
public enum TypeEnum implements Serializable {
	Null("无", 0),
	Gold("金", 10),
	Wood("木", 20),
	Water("水", 30),
	Fire("火", 40),
	Earth("土", 50);
	// All("全", 60);

	private final String name;
	private final int value;

	public static TypeEnum fromValue(int value) {

		for (TypeEnum model : values()) {
			if (model.getValue() == value) {
				return model;
			}
		}
		throw new CoderException("找不到对应的枚举");
	}

	public static TypeEnum fromName(String name) {

		for (TypeEnum model : values()) {
			if (model.getName().equals(name)) {
				return model;
			}
		}
		throw new CoderException("找不到对应的枚举");
	}

	public static Integer randomTypeVal() {
		List<Integer> list = new ArrayList<>();
		for (TypeEnum typeEnum : values()) {
			if (typeEnum.getValue() > 0 && typeEnum.getValue() < 60) {
				list.add(typeEnum.value);
			}
		}
		return PowerRandom.getRandomFromList(list);
	}

	/**
	 * 获取相生的属性
	 *
	 * @param type
	 * @return
	 */
	public static TypeEnum getXiangShengType(int type) {
		switch (type) {
			case 10:
				return TypeEnum.Water;
			case 20:
				return TypeEnum.Fire;
			case 30:
				return TypeEnum.Wood;
			case 40:
				return TypeEnum.Earth;
			case 50:
				return TypeEnum.Gold;
		}
		return TypeEnum.Null;
	}

	/**
	 * 获得type相克的属性
	 * 金＞木＞土＞水＞火＞金
	 *
	 * @param type
	 * @return
	 */
	public static TypeEnum getCounterattackType(int type) {
		switch (type) {
			case 10:
				return TypeEnum.Wood;
			case 20:
				return TypeEnum.Earth;
			case 30:
				return TypeEnum.Fire;
			case 40:
				return TypeEnum.Gold;
			case 50:
				return TypeEnum.Water;
		}
		return TypeEnum.Null;
	}

	/**
	 * 获得克type的属性
	 * 金＞木＞土＞水＞火＞金
	 *
	 * @param type
	 * @return
	 */
	public static TypeEnum getBequeriedType(int type) {
		switch (type) {
			case 10:
				return TypeEnum.Fire;
			case 20:
				return TypeEnum.Gold;
			case 30:
				return TypeEnum.Earth;
			case 40:
				return TypeEnum.Water;
			case 50:
				return TypeEnum.Wood;
		}
		return TypeEnum.Null;
	}

	/**
	 * 获得随机非type所克的属性
	 *
	 * @param type
	 * @return
	 */
	public static int getRandomNotCounterattackType(int type) {
		TypeEnum counterattackType = getCounterattackType(type);
		if (Null == counterattackType) {
			return 0;
		}
		int randomType = 0;
		do {
			randomType = PowerRandom.getRandomBySeed(5) * 10;
		} while (randomType == counterattackType.value);

		return randomType;
	}


	public static void checkProperty(int property) {
		if (property == 0) {
			throw new CoderException("错误的属性：" + property);
		}
		for (TypeEnum model : values()) {
			if (model.getValue() == property) {
				return;
			}
		}
		throw new CoderException("错误的属性：" + property);
	}
}
