package com.bbw.god.city.yed;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 奇遇商品枚举
 * @date 2020/6/2 11:02
 **/
@Getter
@AllArgsConstructor
public enum AdventureGoodsEnum {
	SHSJT(60, "山河社稷图", 12),
	FHL(110, "风火轮", 12),
	ZJBY(320, "紫金钵盂", 0),
	FTY(210, "番天印", 10),
	JLSHZ(260, "九龙神火罩", 10),
	YQL(80, "玉麒麟", 0),
	ZXJ(250, "诛仙剑", 0),
	KXS(50, "捆仙绳", 10),
	DSB(220, "打神鞭", 0),
	MYBJ(270, "莫邪宝剑", 0),
	DSD(420, "定神丹", 0),
	JHL(380, "金葫芦", 0),
	QKC(310, "乾坤尺", 0),
	QXC(120, "七香车", 0),
	WNLS4(840, "四星灵石", 8),
	HDXS(10030, "混沌仙石", 9),
	TTCJ(11010, "通天残卷", 10),
	WNLS5(850, "五星灵石", 6),
	JXQ(10010, "聚仙旗", 5),
	CSZ(10, "财神珠", 0),
	QKT(20, "乾坤图", 6),
	LBJQ(30, "落宝金钱", 0),
	;
	private final Integer goodsId;
	private final String name;
	private final Integer probability;

	public static AdventureGoodsEnum fromName(String name) {
		for (AdventureGoodsEnum adventureGoodsEnum : values()) {
			if (adventureGoodsEnum.getName().equals(name)) {
				return adventureGoodsEnum;
			}
		}
		throw CoderException.high(String.format("没有name=%s的奇遇商品枚举", name));
	}

	public static AdventureGoodsEnum fromGoodsId(int id) {
		for (AdventureGoodsEnum adventureGoodsEnum : values()) {
			if (adventureGoodsEnum.getGoodsId() == id) {
				return adventureGoodsEnum;
			}
		}
		throw CoderException.high(String.format("没有goodsId=%s的奇遇商品枚举", id));
	}

	/**
	 * 获取随机奇遇商品枚举
	 *
	 * @return 随机奇遇商品枚举
	 */
	public static AdventureGoodsEnum getRandom() {
		int offset = PowerRandom.getRandomBySeed(100);
		int sum = 0;
		for (AdventureGoodsEnum adventureGoodsEnum : values()) {
			if (adventureGoodsEnum.getProbability() == 0) {
				continue;
			}
			sum += adventureGoodsEnum.getProbability();
			if (sum >= offset) {
				return adventureGoodsEnum;
			}
		}
		return SHSJT;
	}
}
