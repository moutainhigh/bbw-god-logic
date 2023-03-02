package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 复活	被击退后，有70%的概率回到手牌，手牌已满则进入牌组。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 09:35
 */
@Service
public class BattleSkill1201 extends BattleDieSkill {
	private static final int SKILL_ID = 1201;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
	@Override
	protected Action performSkill(PerformSkillParam psp) {
		Action ar = new Action();
		if (psp.getReceiveEffect() == null || psp.getOppoPlayer().hasBuff(RunesEnum.MIE_PO)) {
			// 伤害已被清除
			return ar;
		}
		if (psp.getReceiveEffect().getSourceID() == CombatSkillEnum.GX.getValue()) {
			return ar;
		}
		//复活	被击退后，有70%的概率回到手牌，手牌已满则进入牌组。
		int probability = 70;// 命中概率
		if (psp.isSeriousInjury()) {
			// 永久性伤害致死 不能复活
			return ar;
		}
		if (!PowerRandom.hitProbability(probability)) {//没有复活灵动
			return ar;
		}
		int fuHuoTimes=psp.getPerformCard().getFuHuoSkillEffectTimes();
		if (fuHuoTimes>=5){
			return ar;
		}
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, psp.getPerformCard().getPos());
		effect.moveTo(PositionType.HAND);
		effect.setAttackPower(AttackPower.L2);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		psp.setReceiveEffect(null);
		psp.getPerformCard().setFuHuoSkillEffectTimes(psp.getPerformCard().getFuHuoSkillEffectTimes()+1);
		return ar;
	}

}
