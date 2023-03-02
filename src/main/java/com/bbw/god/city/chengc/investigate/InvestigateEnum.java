package com.bbw.god.city.chengc.investigate;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 侦查事件枚举类
 * @date 2020/5/29 14:54
 **/
@Getter
@AllArgsConstructor
public enum InvestigateEnum {
	FIGHT("开战", 10, 0,45),
	GAIN_ONE_STAR_TREASURE("随机获得一星法宝", 20, 1,20),
	GAIN_TWO_STAR_TREASURE("随机获得二星法宝", 30,2, 5),
	GAIN_THREE_STAR_TREASURE("随机获得三星法宝", 40,3, 1),
	GAIN_SPECIAL("获得本城池初始特产", 50, 0,15),
	GAIN_ONE_LEVEL_TCP_SPECIAL("获得本城池1级特产铺特产", 60, 1,5),
	GAIN_TWO_LEVEL_TCP_SPECIAL("获得本城池2级特产铺特产", 70, 2,3),
	GAIN_GOLD_10("获得10元宝", 80, 10,3),
	GAIN_GOLD_20("获得20元宝", 90, 20,2),
	GAIN_GOLD_30("获得30元宝", 100,30, 1),
	;
	private String name;
	private int value;
	private int param;//参数
	private int probability;

	public static InvestigateEnum fromName(String name) {
		for (InvestigateEnum investigateEnum : values()) {
			if (investigateEnum.getName().equals(name)) {
				return investigateEnum;
			}
		}
		throw CoderException.high(String.format("没有name=%s的侦查枚举", name));
	}

	public static InvestigateEnum fromValue(int value) {
		for (InvestigateEnum investigateEnum : values()) {
			if (investigateEnum.getValue() == value) {
				return investigateEnum;
			}
		}
		throw CoderException.high(String.format("没有value=%s的侦查枚举", value));
	}

	/**
	 * 获取随机侦查事件
	 *
	 * @return 随机侦查事件
	 */
	public static InvestigateEnum getRandomInvestigateEnum() {
		int offset = PowerRandom.getRandomBySeed(100);
		int sum = 0;
		for (InvestigateEnum investigateEnum : values()) {
			if (investigateEnum.getProbability() == 0) {
				continue;
			}
			sum += investigateEnum.getProbability();
			if (sum >= offset) {
				return investigateEnum;
			}
		}
		return FIGHT;
	}
}
