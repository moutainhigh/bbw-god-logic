package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseService;
import org.springframework.stereotype.Service;

/**
 * 解封：上场时，解除我方全体卡牌封禁状态。且在场时，封禁类技能对我方全体卡牌无效。
 */
@Service
public class BattleSkill3143 extends BattleSkillDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.JIE_FEN.getValue();
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
