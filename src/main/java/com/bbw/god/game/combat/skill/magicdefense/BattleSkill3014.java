package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 连环:在场时，【威风】、【斥退】对我方全体卡牌无效。
 */
@Service
public class BattleSkill3014 extends BattleSkillDefenseService{
	private static final int SKILL_ID = CombatSkillEnum.LIAN_HUAN.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		int targetPos = psp.getReceiveEffect().getTargetPos();

		Action action = super.takeDefense(psp);
		if (action.getTakeEffect()) {
			int seq = psp.getNextAnimationSeq();
			action.addClientAction(ClientAnimationService.getSkillAction(seq, 3006, targetPos));
		}
		return action;
	}
}
