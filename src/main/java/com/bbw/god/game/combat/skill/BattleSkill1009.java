package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 神疗1009  上场时恢复我方召唤师全部血量。
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill1009 extends BattleSkillService {
	private static final int SKILL_ID = 1009;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 神疗1009  上场时恢复我方召唤师全部血量。
		int zhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, zhsPos);
		int roundHp = psp.getPerformPlayer().getMaxHp()-psp.getPerformPlayer().getHp();
		effect.setHp(roundHp);
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
