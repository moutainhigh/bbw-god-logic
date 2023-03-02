package com.bbw.god.game.combat.skill.magicdefense;

import org.springframework.stereotype.Service;

/**
 * 金身：封禁类技能对其无效，自带【金刚】技能。
 */
@Service
public class BattleSkill3015  extends BattleSkillDefenseService{
	private static final int SKILL_ID = 3015;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

}
