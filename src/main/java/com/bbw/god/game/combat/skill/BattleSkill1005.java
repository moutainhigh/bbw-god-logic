package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.card.BattleCard;
import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 1005	混元	上场时本场战斗中敌方的法术值永久损失2。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:07
 */
@Service
public class BattleSkill1005 extends BattleSkillService {
	private static final int SKILL_ID = 1005;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//1005	混元	上场时本场战斗中敌方的法术值永久损失2。
		int oppoZhsPos = PositionService.getZhaoHuanShiPos(psp.getOppoPlayer().getId());
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, oppoZhsPos);
		int roundMp = 2;
		effect.setRoundMp(-roundMp);
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),psp.getPerformCard().getPos(),psp.getOppoZhsPos()));
		return ar;
	}
}
