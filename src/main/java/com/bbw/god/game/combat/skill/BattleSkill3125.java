package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 圣火	每回合破除敌方全体卡牌150~450点防御（不含云台），每升一阶增长50%的效果，无视回光。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:44
 */
@Service
public class BattleSkill3125 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.Sheng4H.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return generateAction(psp, 500);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		int min = 150;
		int max = 450;
		int rInt = PowerRandom.getRandomBetween(min, max);
		return generateAction(psp, rInt);
	}

	private Action generateAction(PerformSkillParam psp, int baseHp) {
		//圣火	每回合破除敌方全体卡牌150~450点防御（不含云台），每升一阶增长50%的效果，无视回光。
		Action attackResult = new Action();
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
		if (oppoPlayingCards.isEmpty()) {
			return attackResult;
		}

		int hp = this.getInt(baseHp * (1 + 0.5 * psp.getPerformCard().getHv()));

		int sequence = psp.getNextAnimationSeq();
		for (BattleCard card : oppoPlayingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setHp(-hp);
			effect.setSequence(sequence);
			attackResult.addEffect(effect);
		}
		return attackResult;
	}
}
