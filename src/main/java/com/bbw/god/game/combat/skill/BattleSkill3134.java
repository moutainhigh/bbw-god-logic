package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 3134	双狙 每回合破除敌方随机2张卡牌160点永久防御值，每升一阶增加50%的效果，无视回光。可攻击到云台位卡牌。（两次可能攻击到同一张卡牌上）
 */
@Service
public class BattleSkill3134 extends BattleSkillService {
	private static final int SKILL_ID = 3134;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 双狙 每回合破除敌方随机2张卡牌100点永久防御值，每升一阶增加50%的效果，无视回光。可攻击到云台位卡牌。（两次可能攻击到同一张卡牌上）
		List<BattleCard> cards=psp.getOppoPlayingCards(true);
		if (cards==null || cards.isEmpty()){
			return  ar;
		}
		List<BattleCard> targetCards=PowerRandom.getRandomsFromList(2,cards);
		List<Effect> effects=new ArrayList<>();
		Double roundHp = 160 * (1 + psp.getPerformCard().getHv() * 0.5);
		int seq=psp.getNextAnimationSeq();
		for (BattleCard card:targetCards){
			//攻击目标卡牌
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setRoundHp(-this.getInt(roundHp));
			effect.setSequence(seq);
			effects.add(effect);
		}
		if (effects.size()==1){
			//单个目标 两次攻击到同一张卡牌上
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, effects.get(0).getTargetPos());
			effect.setRoundHp(-this.getInt(roundHp));
			effects.add(effect);
			AnimationSequence as=ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),SKILL_ID,psp.getPerformCard().getPos(),effect.getTargetPos());
			ar.addClientAction(as);
		}
		ar.addEffects(effects);
		return ar;
	}
}
