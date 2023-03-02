package com.bbw.god.game.config.city;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 世界格子建筑类型
 * 
 * @author suhq
 * @version 创建时间：2018年9月21日 下午3:10:13
 *
 */
@Getter
@AllArgsConstructor
public enum CityTypeEnum implements Serializable {
	CZ("村庄", 10),
	YD("野地", 20),
	FD("福地", 30),
	FD2("福地", 33),
	FD3("福地", 36),
	JB("界碑", 40),
	XRD("仙人洞", 50),
	KZ("客栈", 60),
	YSG("游商馆", 70),
	FLX("福临轩", 80),
	HEIS("黑市", 100),
	KS("矿山", 110),
	SL("森林", 120),
	HP("湖泊", 130),
	HuoS("火山", 140),
	NZ("泥沼", 150),
	NWM("女娲庙", 160),
	MY("庙宇", 170),
	TYF("太一府", 180),
	MXD("迷仙洞", 190),
	LT("鹿台", 200),
	CC1("1级城池", 210),
	CC2("2级城池", 220),
	CC3("3级城池", 230),
	CC4("4级城池", 240),
	CC5("主城", 250);
	private String name;
	private int value;

	public static CityTypeEnum fromValue(int value) {
		for (CityTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}