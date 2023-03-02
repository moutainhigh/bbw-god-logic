package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 3110	噬魂	每回合对敌方召唤师造成星级*150攻击力的伤害，每升一阶增加40%的效果
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 03:00
 */
@Service
public class BattleSkill3110 extends BattleSkillService {
	private static final int SKILL_ID = 3110;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return generateAction(psp, 160);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		return generateAction(psp, 150);
	}

	private Action generateAction(PerformSkillParam psp, int baseHp) {
		Action ar = new Action();
		int sequence = psp.getNextAnimationSeq();
		Effect effect = getEffect(psp.getPerformCard(), baseHp, psp.getOppoZhsPos(), sequence);
		ar.addEffect(effect);
		return ar;
	}

	public Effect getEffect(BattleCard performCard, int baseHp, int oppZhsPos, int sequence) {
		//攻击目标卡牌
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, oppZhsPos);
		int hp = baseHp * performCard.getStars();
		hp = this.getInt(hp * (1 + 0.4 * performCard.getHv()));
		effect.setHp(-hp);
		effect.setSequence(sequence);
		return effect;
	}
}
