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
 * 绝杀：上场时，移除敌方场上随机1张卡牌，无视回光，此卡牌无法发动死亡技能，且无法进入坟场。
 * 
 * @author lwb
 * @date 2020年3月9日
 * @version 1.0
 */
@Service
public class BattleSkill1011 extends BattleSkillService {
	private static final int SKILL_ID = 1011;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 绝杀：上场时，移除敌方场上随机1张卡牌，无视回光，此卡牌无法发动死亡技能，且无法进入坟场。
		Optional<BattleCard> target = psp.randomOppoPlayingCard(false);
		if (!target.isPresent()) {
			return ar;
		}
		ar.setTakeEffect(true);
		BattleCard targetCard = target.get();
		// 无法进入坟场=>丢入异次元
		targetCard.banAllDieSKill();
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, targetCard.getPos());
		effect.moveTo(PositionType.DEGENERATOR);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
