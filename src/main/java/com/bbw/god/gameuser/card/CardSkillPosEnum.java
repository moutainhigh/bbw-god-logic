package com.bbw.god.gameuser.card;

import com.bbw.exception.ExceptionForClientTip;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卡牌技能位置枚举
 *
 * @author 0116
 * @create: 2022-08-25 11:49
 */
@AllArgsConstructor
@Getter
public enum CardSkillPosEnum {
	SKILL_0(0,"0级技能"),
	SKILL_5(5,"5级技能"),
	SKILL_10(10,"10级技能"),

	;
	
	private int skillPos;
	private String describe;

	/**
	 * 根据 key 获取枚举
	 * @param skillPos
	 * @return
	 */
	public static CardSkillPosEnum fromSkillPos(int skillPos) {
		for (CardSkillPosEnum way:values()) {
			if (way.getSkillPos() == skillPos) {
				return way;
			}
		}
		throw new ExceptionForClientTip("card.skill.pos.enum.error");
	}

	/**
	 * 根据等级获取对应的key ==>获取枚举
	 * @param level
	 * @return
	 */
	public static CardSkillPosEnum fromLevel(int level) {

		int skillPos;

		if (level < 5) {
			skillPos = 0;
		}else if (level < 10){
			skillPos = 5;
		}else {
			skillPos = 10;
		}

		for (CardSkillPosEnum way:values()) {
			if (way.getSkillPos() == skillPos) {
				return way;
			}
		}
		throw new ExceptionForClientTip("card.skill.pos.enum.error");
	}
}
