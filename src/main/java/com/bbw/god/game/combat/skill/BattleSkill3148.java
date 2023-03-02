package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 冷刀（3148）：每回合随机将敌方一张手牌送入坟场。
 *
 */
@Service
public class BattleSkill3148 extends BattleSkillService {
	private static final int SKILL_ID = 3148;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//每回合随机将敌方一张手牌送入坟场。
		BattleCard[] handCards=psp.getOppoPlayer().getHandCards();
		List<BattleCard> validCards=new ArrayList<>();
		for (BattleCard card:handCards){
			if (card!=null){
				validCards.add(card);
			}
		}
		if (ListUtil.isEmpty(validCards)) {
			return ar;
		}
		BattleCard targetCard= PowerRandom.getRandomFromList(validCards);
		CardPositionEffect effect=CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(),targetCard.getPos());
		effect.setSequence(psp.getNextAnimationSeq());
		effect.setToPositionType(PositionType.DISCARD);
		ar.addEffect(effect);
		return ar;
	}
}
