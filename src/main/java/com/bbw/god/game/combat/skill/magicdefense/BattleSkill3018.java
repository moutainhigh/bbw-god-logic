package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 璧环 3018：在场时，【威风】、【斥退】、【枷锁】、【连锁】对我方全体卡牌无效。
 *
 * @author: suhq
 * @date: 2021/9/29 3:00 下午
 */
@Service
public class BattleSkill3018 extends BattleSkillDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.BI_HUAN.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		int targetPos = psp.getReceiveEffect().getTargetPos();
		int seq = psp.getNextAnimationSeq();
		Action action = super.takeDefense(psp);
		if (action.getTakeEffect()) {
			action.addClientAction(ClientAnimationService.getSkillAction(seq, 3006, targetPos));
		}
		return action;
	}
}
