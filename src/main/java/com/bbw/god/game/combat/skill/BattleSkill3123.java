package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.TypeEnum;

/**
 * 强土
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:33
 */
@Service
public class BattleSkill3123 extends BattleSkill31193123 {
	private static final int SKILL_ID = 3123;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public TypeEnum getType() {
		return TypeEnum.Earth;
	}
}
