package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 *  度升3161： 每回合，我方云台位没有卡牌时，将我方1张地面卡牌移到云台位。
 * （1）随机的对象包括自身。
 * （2）将自身移到云台位时，会接着释放度升之后的技能。
 *
 * @author liuwenbin
 */
@Service
public class BattleSkill3161 extends BattleSkillService {
	private static final int SKILL_ID = 3161;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		if (!psp.getPerformPlayer().yunTaiIsEmpty()) {
			return ar;
		}
		//将我方1张地面卡牌移到云台位。随机的对象包括自身,将自身移到云台位时，会接着释放度升之后的技能。
		List<BattleCard> cards = psp.getPerformPlayer().getPlayingCards(false);
		BattleCard targetCard = PowerRandom.getRandomFromList(cards);
		int yunTaiPos = PositionService.getYunTaiPos(psp.getPerformPlayerId());
		CardPositionEffect effect=CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(),targetCard.getPos());
		effect.moveTo(PositionType.BATTLE,yunTaiPos);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
