package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 倾国3163 每回合，对敌方2张地面卡牌施放【魅惑】，无视【回光】。
 */
@Service
public class BattleSkill3163 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.QING_GUO.getValue();// 技能ID
	@Autowired
	private BattleSkill3106 battleSkill3106;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cards = psp.getOppoPlayingCards(false);
		if (ListUtil.isEmpty(cards)) {
			return ar;
		}
		cards = cards.stream()
				.filter(p -> !p.existSkillStatus(SKILL_ID) && !p.existSkillStatus(CombatSkillEnum.MH.getValue()))
				.collect(Collectors.toList());
		if (ListUtil.isEmpty(cards)) {
			return ar;
		}
		int performPos = psp.getPerformCard().getPos();
		int effectSeq = psp.getNextAnimationSeq();
		int attackSeq = psp.getNextAnimationSeq();
		int statusSeq = psp.getNextAnimationSeq();
		int oppoZhsPos = psp.getOppoZhsPos();
		int effectNum = cards.size() >= 2 ? 2 : 1;
		List<BattleCard> targetCards = PowerRandom.getRandomsFromList(effectNum, cards);
		for (BattleCard targetCard : targetCards) {
			BattleSkillEffect effect = battleSkill3106.getEffect(effectSeq, targetCard, targetCard.getPos(), oppoZhsPos);
			AnimationSequence skillAction = ClientAnimationService.getSkillAction(attackSeq, getMySkillId(), performPos, targetCard.getPos());
			ar.addClientAction(skillAction);
			AnimationSequence statusEffectAction = ClientAnimationService.getStautsEffectAction(effect);
			statusEffectAction.setSeq(statusSeq);
			ar.addClientAction(statusEffectAction);

			effect.replaceEffectSkillId(getMySkillId());
			effect.setSourcePos(psp.getPerformCard().getPos());
			effect.setAttackPower(Effect.AttackPower.L1);
			ar.addEffect(effect);
		}
		ar.setNeedAddAnimation(false);
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,int attkPos) {
		int ZhsPos=fromPsp.getOppoZhsPos();
		BattleCard myCard =fromPsp.getCombat().getBattleCard(attkPos);
		int sequence = fromPsp.getNextAnimationSeq();
		List<Effect> effects=new ArrayList<>();
		effects.add(battleSkill3106.getEffect(sequence, myCard, attkPos, ZhsPos));
		return effects;
	}

}
