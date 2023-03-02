package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import org.springframework.stereotype.Service;

/**
 * 法身：封禁类技能对其无效。
 */
@Service
public class BattleSkill3013 extends BattleSkillDefenseService{
	private static final int SKILL_ID = CombatSkillEnum.FA_SHEN.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
