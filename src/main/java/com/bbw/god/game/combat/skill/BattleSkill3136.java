package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 鼓舞:每回合随机选择我方场上1张卡牌，该回合卡牌的攻击力提高50%。每升三阶，可多鼓舞1张卡牌。（最多4张）
 * 
 * @author lwb
 * @date 2019年12月16日
 * @version 1.0
 */
@Service
public class BattleSkill3136 extends BattleSkillService {
	private static final int SKILL_ID = 3136;//技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		//鼓舞: 每回合随机选择我方场上1张卡牌，该回合卡牌的攻击力提高50%。每升五阶，可多鼓舞一张卡牌（最多三张）。
		Action ar = new Action();
	    BattleCard performCard=psp.getPerformCard();
		int num = performCard.getHv() / 3 + 1;
		num = Math.min(num, 4);
	    List<BattleCard> cards=psp.getMyPlayingCards(true);
	    num=Math.min(num, cards.size());
	    List<BattleCard> effectCards=PowerRandom.getRandomsFromList(num, cards);
	    int seq=psp.getNextAnimationSeq();
	    for (BattleCard card:effectCards) {
	    	CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setAtk(getInt(card.getAtk()*0.5));
			effect.setSequence(seq);
			ar.addEffect(effect);
		}
	    AnimationSequence action=ClientAnimationService.getSkillAction(seq, SKILL_ID, performCard.getPos(),performCard.getPos() );
		ar.addClientAction(action);
	    return ar;
	}
}
