package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 3102 拦截 每回合破除敌方云台位置的卡牌自身星级*(50~100)点永久防御，每升一阶增长50%的效果。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 23:10
 */
@Service
public class BattleSkill3102 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.LJ.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		// 军师位置 正确计算方式为星级*150*(1+0.5*阶级)
		Double hp = 150 * psp.getPerformCard().getStars()
				* (1 + 0.5 * psp.getPerformCard().getHv());
		return generateAction(psp, this.getInt(hp));
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Double min = 50 * psp.getPerformCard().getStars()
				* (1 + 0.5 * psp.getPerformCard().getHv());
		Double max = 100 * psp.getPerformCard().getStars()
				* (1 + 0.5 * psp.getPerformCard().getHv());
		int roundHp = PowerRandom.getRandomBetween(this.getInt(min),
				this.getInt(max));
		return generateAction(psp, roundHp);
	}

	private Action generateAction(PerformSkillParam psp, int roundHp) {
		Action ar = new Action();
		// 3102 拦截 每回合破除敌方云台位置的卡牌自身星级*(50~100)点永久防御，每升一阶增长50%的效果。
		BattleCard[] oppoPlayingCards = psp.getOppoPlayer().getPlayingCards();
		if (null == oppoPlayingCards[0]) {
			return ar;
		}
		BattleCard targetCard = oppoPlayingCards[0];
		// 攻击目标卡牌
		CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(),
				targetCard.getPos());

		effect.setRoundHp(-roundHp);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);

		return ar;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		return productAction(psp);
	}
}
