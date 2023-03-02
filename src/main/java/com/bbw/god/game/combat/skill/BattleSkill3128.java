package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 灭魄 每回合随机移除敌方墓地的一张卡牌
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill3128 extends BattleSkillService {
	private static final int SKILL_ID = 3128;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		//灭魄 每回合随机移除敌方墓地的一张卡牌
		Action action = new Action();
		List<BattleCard> discards =psp.getOppoPlayer().getDiscard();
		if (null==discards || discards.isEmpty()) {
			return action;
		}
		BattleCard card=PowerRandom.getRandomFromList(discards);
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, card.getPos());
		effect.moveTo(PositionType.DEGENERATOR);
		effect.setSequence(psp.getNextAnimationSeq());
		action.addEffect(effect);
		return action;
	}
}
