package com.bbw.god.game.combat.nskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 连击4451：连击4451：攻击时，有50%概率攻击两次，每阶增加5%概率。
 */
@Service
public class BattleSkill4451 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.LIAN_JI.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		BattleCard performCard = psp.getPerformCard();
		//加锁、混绫不发动
		int banFrom = performCard.getNormalAttackSkill().getTimesLimit().getBanFrom();
		if (banFrom == CombatSkillEnum.LIAN_SUO.getValue()
				|| banFrom == CombatSkillEnum.JS.getValue()
				|| banFrom == CombatSkillEnum.HUN_LING.getValue()) {
			return action;
		}
		int seed = performCard.getHv() * 5 + 50;
		if (PowerRandom.hitProbability(seed)) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, performCard.getPos());
			effect.setAtk(performCard.getNormalAttackPreAtk() - performCard.getAtk());
			action.addEffect(effect);
			performCard.getNormalAttackSkill().getTimesLimit().setCurrentRoundTimes(1);
		}
		return action;
	}
}
