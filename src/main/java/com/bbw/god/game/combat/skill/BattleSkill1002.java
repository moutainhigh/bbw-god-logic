package com.bbw.god.game.combat.skill;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 1002 妖术 上场时敌方场上（不含云台）随机1张卡牌进坟场。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:02
 */
@Service
public class BattleSkill1002 extends BattleSkillService {
	private static final int SKILL_ID = 1002;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 1002 妖术 上场时敌方场上（不含云台）随机1张卡牌进坟场。
		Optional<BattleCard> targetCard = psp.randomOppoPlayingCard(false);
		if (!targetCard.isPresent()) {
			return ar;
		}
		// 攻击目标卡牌
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, targetCard.get().getPos());
		effect.moveTo(PositionType.DISCARD);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
