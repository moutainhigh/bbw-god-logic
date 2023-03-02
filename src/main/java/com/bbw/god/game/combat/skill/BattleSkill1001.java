package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 斥退。上场时敌方场上随机3张卡牌回到牌组。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 10:25
 */
@Service
public class BattleSkill1001 extends BattleSkillService {
	private static final int SKILL_ID = 1001;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//斥退。上场时敌方场上随机3张卡牌回到牌组。
		List<BattleCard> targetCards = psp.randomOppoPlayingCards(3, false);
		if (targetCards.isEmpty()) {
			return ar;
		}
		int seq=psp.getNextAnimationSeq();
		//攻击目标卡牌
		for (BattleCard card : targetCards) {
			CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, card.getPos());
			effect.setSequence(psp.getNextAnimationSeq());
			effect.moveTo(PositionType.DRAWCARD);
			ar.addEffect(effect);
			//特殊情况  生成动画，因为只斥退需要释放一次，但是卡牌移动分3次移动
			//因此3个effect的序列号不一样
			AnimationSequence animation = ClientAnimationService.getSkillAction(seq,SKILL_ID,psp.getPerformCard().getPos(),card.getPos());
			ar.addClientAction(animation);
		}
		return ar;
	}

}
