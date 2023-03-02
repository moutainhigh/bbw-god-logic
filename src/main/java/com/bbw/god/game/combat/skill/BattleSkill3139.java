package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 火球（3139）：每回合破除敌方1张卡牌150点防御值，每升一阶增加50%效果。
 * 
 * @author lwb
 * @date 2020年02月19日
 * @version 1.0
 */
@Service
public class BattleSkill3139 extends BattleSkillService {
	private static final int SKILL_ID = 3139;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		Optional<BattleCard> targetOp=psp.randomOppoPlayingCard(false);
		if (!targetOp.isPresent()) {
			return ar;
		}
		//每回合破除敌方1张卡牌150点防御值，每升一阶增加50%效果。
		CardValueEffect effect = attackEffect(card, targetOp.get());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}

	public CardValueEffect attackEffect(BattleCard performCard,BattleCard targetCard){
		int hp=getInt(150*(1+performCard.getHv()*0.5));
		CardValueEffect cardValueEffect=CardValueEffect.getSkillEffect(SKILL_ID,targetCard.getPos());
		cardValueEffect.setHp(-hp);
		return cardValueEffect;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//每回合破除敌方1张卡牌150点防御值，每升一阶增加50%效果。
		CardValueEffect effect = attackEffect(psp.getPerformCard(), target);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
