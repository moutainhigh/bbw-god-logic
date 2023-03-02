package com.bbw.god.game.combat.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年10月10日 下午2:05:03 
* 类说明  胜利结果枚举
*/
@AllArgsConstructor
@Getter
public enum CombatResultEnum {
	NO_END(0, "未结束"),
	HP_EMPTY(1, "召唤师没血了"),
	CARD_EMPTY(2, "一方无卡牌"),
	ROUND_TIMEOUT(3, "超过最大回合数");

	private int val;
	private String memo;


	public static CombatResultEnum fromValue(int value) {
		for (CombatResultEnum item : values()) {
			if (item.getVal() == value) {
				return item;
			}
		}
		return null;
	}
}
