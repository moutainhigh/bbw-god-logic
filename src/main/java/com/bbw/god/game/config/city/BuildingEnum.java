package com.bbw.god.game.config.city;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 城内建筑类型
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:07:13
 *
 */
@Getter
@AllArgsConstructor
public enum BuildingEnum {

	FY("府衙", 10),
	KC("矿场", 20),
	QZ("钱庄", 30),
	TCP("特产铺", 40),
	JXZ("聚贤庄", 50),
	LBL("炼宝炉", 60),
	DC("道场", 70),
	LDF("炼丹房", 80),
	FT("法坛", 90),
	;

	private String name;
	private int value;

	public static BuildingEnum fromValue(int value) {
		for (BuildingEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
