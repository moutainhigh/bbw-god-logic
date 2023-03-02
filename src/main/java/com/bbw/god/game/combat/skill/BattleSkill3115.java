package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.TypeEnum;

/**
 * 生木	首回合，我方战场上所有木属性卡牌防御力永久上升其本身星级*80，后续每回合防御力永久上升星级*30。每升一阶增加30%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:33
 */
@Service
public class BattleSkill3115 extends BattleSkill31143118 {
	private static final int SKILL_ID = 3115;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public TypeEnum getType() {
		return TypeEnum.Wood;
	}
}
