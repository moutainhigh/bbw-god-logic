package com.bbw.god.game.combat.skill.magicdefense;

import org.springframework.stereotype.Service;

/**
 * 乾坤: 封禁类技能对其无效，TODO: 自带【复活】技能。
 */
@Service
public class BattleSkill3012 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 3012;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
