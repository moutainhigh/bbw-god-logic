package com.bbw.god.game.config.special;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 特产
 * @author suhq
 * @version 创建时间：2018年9月21日 下午3:19:14
 *
 */
@Getter
@AllArgsConstructor
public enum SpecialEnum implements Serializable {

	YAN("盐", 1, 20), MIAN("面", 2, 10),
	BC("白菜", 3, 40), YM("亚麻", 4, 30),
	CL("草料", 5, 40), YU("鱼", 6, 20),
	SL("石料", 7, 50), XR("鲜肉", 8, 30),
	MAO("猫", 9, 10), TQ("陶器", 10, 50),

	GJ("龟甲", 11, 10), DD("肚兜", 12, 20),
	CQ("瓷器", 13, 30), YRY("玉如意", 14, 40),
	GD("宫灯", 15, 50), QTQ("青铜器", 16, 20),
	SX("麝香", 17, 40), HQ("狐裘", 18, 30),
	SQ("丝绸", 19, 10), ZWBZ("纣王扳指", 20, 50),

	SJ("水晶", 21, 30), LJ("鹿角", 22, 20),
	ML("玛瑙", 23, 10), FC("翡翠", 24, 40),
	ZZWBZ("真·纣王扳指", 25, 50),

	HP("海珀", 31, 20), LZ("龙珠", 32, 20),
	JQG("金钱卦", 33, 10), JJ("金鼎", 34, 10),
	HJ("火晶", 35, 40), HZ("黄钟", 36, 40),
	XL("香料", 37, 30), SXJ("神仙酒", 38, 30),
	YGSD("御贡神丹", 39, 50), ZWG("纣王戈", 40, 50),

	HLM("幻狸猫", 41, 50), YG("御羹", 42, 50),
	NV_ER_JIN("女儿巾", 43, 50), QTD("青铜灯", 44, 50),
	YMT("玉满堂", 45, 50), LING_ZHU("灵珠", 46, 50),
	;

	private final String name;
	private final Integer value;
	private final Integer country;//西 金 10；东  木 20；北 水 30；南 火 40；中 土50

	public static SpecialEnum fromValue(int value) {
		for (SpecialEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
