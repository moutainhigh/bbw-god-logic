package com.bbw.god.gameuser.achievement;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 成就状态
 * 
 * @author suhq
 * @date 2019年2月21日 上午11:18:46
 */
@Getter
@AllArgsConstructor
public enum AchievementTypeEnum {

	PERSON("个人", 10),
	CARD("卡牌", 20),
	BUSINESS("商途", 30),
	ATTACK("征战", 40),
	SPORTS("竞技", 50),
	EXPERIENCE("历练", 60),
	SECRET("秘闻", 70),
	HEXAGRAM("卦象",80),
	;

	private String name;
	private int value;

	public static AchievementTypeEnum fromValue(int value) {
		for (AchievementTypeEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}
