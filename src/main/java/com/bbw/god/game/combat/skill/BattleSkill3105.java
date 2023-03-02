package com.bbw.god.game.combat.skill;

import java.util.List;

import com.bbw.common.JSONUtil;
import org.springframework.stereotype.Service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 3105	回魂	每回合随机将1张我方坟场的卡牌拉回牌组。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 00:43
 */
@Service
public class BattleSkill3105 extends BattleSkillService {
	private static final int SKILL_ID = 3105;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//3105	回魂	每回合随机将1张我方坟场的卡牌拉回牌组。
		List<BattleCard> discards = psp.getPerformPlayer().getDiscard();
		if (discards.isEmpty()) {
			return ar;
		}
		//随机将1张我方坟场的卡牌
		BattleCard card=PowerRandom.getRandomFromList(discards);
		//拉回战场
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, card.getPos());
		effect.moveTo(PositionType.DRAWCARD);
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);

		return ar;
	}
}
