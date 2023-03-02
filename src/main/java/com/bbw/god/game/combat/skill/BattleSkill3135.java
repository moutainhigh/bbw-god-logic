package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 震慑：每回合可随机指定敌方场上1张卡牌（不含云台），该回合卡牌的攻击力减半。每升三阶，可多震慑1张卡牌（最多4张）。
 * 
 * @author lwb
 * @date 2020年03月09日
 * @version 1.0
 */
@Service
public class BattleSkill3135 extends BattleSkillService {
	private static final int SKILL_ID = 3135;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 每回合可随机指定敌方场上1张卡牌（不含云台），该回合卡牌的攻击力减半。每升三阶，可多震慑1张卡牌（最多4张）。
		Action ar = new Action();
		List<BattleCard> playingCards = psp.getOppoPlayingCards(false);
		if (playingCards.isEmpty()) {
			return ar;
		}
	    BattleCard performCard=psp.getPerformCard();
		int num = performCard.getHv() / 3 + 1;
		num = Math.min(num, 4);
		num = Math.min(num, playingCards.size());
		List<BattleCard> effectCards = PowerRandom.getRandomsFromList(num, playingCards);
	    int seq=psp.getNextAnimationSeq();
	    for (BattleCard card:effectCards) {
	    	CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setAtkTimes(-0.5);
			effect.setSequence(seq);
			ar.addEffect(effect);
		}
	    return ar;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
		effect.setAtkTimes(-0.5);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
