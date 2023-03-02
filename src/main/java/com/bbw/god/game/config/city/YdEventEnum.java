package com.bbw.god.game.config.city;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 野地事件类型
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:07:13
 */
@Getter
@AllArgsConstructor
public enum YdEventEnum {

	NONE("无事", 1),
	TAX("税收", 2),
	KC("矿场元素", 3),
	SBX("四不像", 4),
	QL("青鸾", 5),
	PICK_COPPER("捡到铜钱", 6),
	DA_BING("大兵", 7),
	XIAO_BAI("小白", 8),
	LAO_NAI_NAI("老奶奶送特产", 9),
	XIAN_REN("仙人送法宝", 10),
	DICE("小灶体力", 11),
	DA_MA("大妈", 12),
	XIAO_TOU("小偷", 13),
	QIANG_DAO("强盗", 14),
	TU_HAO("土豪", 15),
	LUCKY_NUM("幸运号", 16),
	CHUAN_SONG("传送", 17),
	YAYALE_TIP("押押乐元素", 18),
	XRSY("仙人授业", 19),
	YYSR("云游商人", 20),
	ZJZF("竹间之风", 21),
	TSJF("天师借法", 22),
	XIN_MO("造化弄人", 23),
	JIN_WEI_JUN("匪患猖獗", 24),
	ZSYJ("诸神遗迹", 25),
	HC("回城", 26),
	CBNX("寸步难行", 27);

	private final String name;
	private final int value;

	public static YdEventEnum fromValue(int value) {
		for (YdEventEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

	public static YdEventEnum fromName(String name) {
		for (YdEventEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 获取负面事件集合
	 *
	 * @return
	 */
	public static List<YdEventEnum> getDebuffEventList() {
		return Arrays.asList(DA_BING, DA_MA, XIAO_TOU, QIANG_DAO);
	}
}
