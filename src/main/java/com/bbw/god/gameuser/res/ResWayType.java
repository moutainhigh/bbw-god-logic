package com.bbw.god.gameuser.res;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 游戏中资源获得分类
 * 
 * @author suhq
 * @date 2019-09-20 16:08:51
 */
@Getter
@AllArgsConstructor
public enum ResWayType implements Serializable {

	Normal("常规", 1),
	God("神仙加成", 2),
	CaiSZ("财神珠加成", 3),
	ZhaoC("招财加成", 4),
	LingY("灵印加成", 5),
	YuanQD("元气丹加成", 6),
	DaoC("道场加成", 7),
	Coc("商会加成", 8),
	Activity("活动加成", 9),
	Presentation("赠送", 10),
	Extra("常规以外的额外加成",11),
	NightmareBuff("梦魇加成",12),
	DOUBLE_EXP_MEDICINE("双倍经验丹",13);
	
	private String name;
	private int value;

	public static ResWayType fromValue(int value) {
		for (ResWayType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		throw CoderException.high("无效的途径");
	}

	public static ResWayType fromName(String name) {
		for (ResWayType item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		throw CoderException.high("无效的途径");
	}
}