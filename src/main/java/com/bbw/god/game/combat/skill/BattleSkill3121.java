package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.TypeEnum;

/**
 * 强水
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:33
 */
@Service
public class BattleSkill3121 extends BattleSkill31193123 {
	private static final int SKILL_ID = 3121;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public TypeEnum getType() {
		return TypeEnum.Water;
	}
}
