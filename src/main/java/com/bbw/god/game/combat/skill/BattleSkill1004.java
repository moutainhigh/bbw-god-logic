package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 1004	修仙	上场时本场战斗中自身的法术值永久增加1。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:07
 */
@Service
public class BattleSkill1004 extends BattleSkillService {
	private static final int SKILL_ID = 1004;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//1004	修仙	上场时本场战斗中自身的法术值永久增加1。
		int zhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, zhsPos);
		int roundMp = 1;
		effect.setRoundMp(roundMp);
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
