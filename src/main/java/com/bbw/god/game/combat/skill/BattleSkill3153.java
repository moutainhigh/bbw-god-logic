package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 * 镜像（3153）：每回合有40%的概率将自身攻防变化为与对位卡牌基础攻防等同。每升一阶提高5%的概率。
 *
 */
@Service
public class BattleSkill3153 extends BattleSkillService {
	private static final int SKILL_ID = 3153;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int seed=40+psp.getPerformCard().getHv()*5;
		if (!PowerRandom.hitProbability(seed)){
			return ar;
		}
		Optional<BattleCard> faceToFaceCardOp = psp.getFaceToFaceCard();
		if (!faceToFaceCardOp.isPresent()){
			return ar;
		}
		BattleCard faceToFaceCard=faceToFaceCardOp.get();
		int hp=faceToFaceCard.getRoundHp()-psp.getPerformCard().getHp();
		int atk=faceToFaceCard.getRoundAtk()-psp.getPerformCard().getAtk();
		CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getMySkillId(),psp.getPerformCard().getPos());
		valueEffect.setHp(hp);
		valueEffect.setAtk(atk);
		ar.addEffect(valueEffect);
		return ar;
	}
}
