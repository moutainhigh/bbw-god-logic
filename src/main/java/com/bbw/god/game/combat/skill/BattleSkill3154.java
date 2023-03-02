package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 *
 * 偷营（3154）：下回合敌方手牌上限降低为4张，每升五阶再少1张，超出的手牌将会随机洗入牌组。
 *
 */
@Service
public class BattleSkill3154 extends BattleSkillService {
	private static final int SKILL_ID = 3154;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int handCardLimit=5-(1+psp.getPerformCard().getHv()/5);
		psp.getOppoPlayer().getStatistics().setHandCardUpLimit(handCardLimit);
		ar.setTakeEffect(true);
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),psp.getPerformCard().getPos(),psp.getOppoZhsPos()));
		return ar;
	}
}
