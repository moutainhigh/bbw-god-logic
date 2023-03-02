package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 火枪：
 * 攻击前，破除敌方卡牌150点防御值，每升一阶增加50%效果。自带【奇袭】、【穿刺】技能。
 * 
 */
@Service
public class BattleSkill3901 extends BattleSkillService {
	private static final int SKILL_ID = 3901;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		Optional<BattleCard> faceToFaceCard = psp.getFaceToFaceCard();
		if (faceToFaceCard.isPresent()){
			//每回合破除敌方1张卡牌150点防御值，每升一阶增加50%效果。
			CardValueEffect effect = attackEffect(psp.getPerformCard(),faceToFaceCard.get());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
		}
		return ar;
	}

	public CardValueEffect attackEffect(BattleCard performCard,BattleCard targetCard){
		int hp=getInt(150*(1+performCard.getHv()*0.5));
		CardValueEffect cardValueEffect=CardValueEffect.getSkillEffect(SKILL_ID,targetCard.getPos());
		cardValueEffect.setHp(-hp);
		return cardValueEffect;
	}
}
