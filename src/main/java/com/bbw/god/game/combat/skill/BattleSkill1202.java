package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 死士	被击退后，对敌方召唤师造成等同于自身攻击力两倍的伤害。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill1202 extends BattleDieSkill {
	private static final int SKILL_ID = 1202;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action performSkill(PerformSkillParam psp) {
		Action ar = new Action();
		//死士	被击退后，对敌方召唤师造成等同于自身攻击力两倍的伤害。
		int targetPos = PositionService.getZhaoHuanShiPos(psp.getOppoPlayer().getId());
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, targetPos);
		int atk = psp.getPerformCard().getAtk() * 2;
		effect.setHp(-atk);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
