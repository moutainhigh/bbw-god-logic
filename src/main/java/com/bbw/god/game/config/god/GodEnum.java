package com.bbw.god.game.config.god;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 神仙 type 10 按步数 20 按时间 30 下一城战 40 一次性
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 下午3:12:19
 *
 */
@Getter
@AllArgsConstructor
public enum GodEnum implements Serializable {

	XCS("小财神", 10, 10, 20), // 生效步数
	DCS("大财神", 10, 20, 20),
	XFS("小福神", 10, 30, 20),
	DFS("大福神", 10, 40, 20),
	SZGY("送子观音", 10, 50, 20),
	TB("天兵", 30, 60, 1), // 下个城战
	TJ("天将", 30, 70, 1),
	XZ("仙长", 20, 80, 21600), // 秒数,6个小时
	XB("虾兵", 30, 90, 1),
	XJ("蟹将", 30, 100, 1),
	SS("衰神", 20, 110, 21600), // 秒数,6个小时
	SIS("死神", 10, 120, 20),
	QS("穷神", 10, 130, 20),
	BBX("百宝箱", 40, 520, 0);//
	private String name;
	private int type;
	private int value;
	private int effect;

	public static GodEnum fromValue(int value) {
		for (GodEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

	public boolean isBuffGod() {
		switch (this) {
			case XCS:
			case DCS:
			case XFS:
			case DFS:
			case SZGY:
			case TB:
			case TJ:
			case XZ:
				return true;
			default:
				return false;
		}
	}

	public boolean isDebuffGod() {
		switch (this) {
			case XB:
			case XJ:
			case SS:
			case SIS:
			case QS:
				return true;
			default:
				return false;
		}
	}
}