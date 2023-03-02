package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 升仙1008  上场时本场战斗中自身的法术值永久增加2。
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill1008 extends BattleSkillService {
	private static final int SKILL_ID = 1008;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//升仙1008  上场时本场战斗中自身的法术值永久增加2。
		int zhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, zhsPos);
		int roundMp = 2;
		effect.setRoundMp(roundMp);
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
