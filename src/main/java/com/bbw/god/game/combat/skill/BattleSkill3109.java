package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 业火	每回合破除敌方全体卡牌（不含云台）50~150点防御，每升一阶增长50%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 02:45
 */
@Service
public class BattleSkill3109 extends BattleSkillService {
	private static final int SKILL_ID = 3109;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return generateAction(psp, 200);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		int min = 50;
		int max = 150;
		int rInt = PowerRandom.getRandomBetween(min, max);
		return generateAction(psp, rInt);
	}

	private Action generateAction(PerformSkillParam psp, int baseHp) {
		Action attackResult = new Action();
		// 业火	每回合破除敌方全体卡牌（不含云台）50~150点防御，每升一阶增长50%的效果。
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
