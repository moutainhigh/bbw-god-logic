package com.bbw.god.game.combat.nskill;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【龙麟】：受到法术攻击时，有x%（受韧性影响，概率上限60%）概率减少受到的x%（受强度影响，减少伤害上限60%）伤害。
 *
 * @author: suhq
 * @date: 2022/9/24 2:03 下午
 */
@Service
public class BattleSkill11102 implements ISkillDefenseService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.LONG_LIN_WEAPON.getValue();
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		ar.setTakeEffect(false);
		if (psp.getReceiveEffect() == null) {
			return ar;
		}
		if (!psp.getReceiveEffect().isValueEffect()) {
			return ar;
		}
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return ar;
		}
		double deductRate = cardEquipmentSkillService.getExtraSkillRate(psp.getPerformCard(), getMySkillId());

		CardValueEffect effect = CloneUtil.clone(psp.getReceiveEffect().toValueEffect());
		effect.addExtraSkillEffect(getMySkillId());
		if (effect.getHp() > 0 || effect.getRoundHp() > 0) {
			//增益效果
			return ar;
		}
		ar.getEffects().clear();
		if (effect.getHp() != 0) {
			int newHp = (int) (effect.getHp() * (1 - deductRate));
			effect.setHp(newHp);
		}
		if (effect.getRoundHp() != 0) {
			int newRoundHp = (int) (effect.getRoundHp() * (1 - deductRate));
			effect.setRoundHp(newRoundHp);
		}
		ar.addEffect(effect);
		if (effect.getValueType().getType() == CardValueEffect.CardValueEffectType.LASTING.getType()) {
			effect.setValueType(CardValueEffect.CardValueEffectType.IN_TIME);
			CardValueEffect cardValueEffect = psp.getReceiveEffect().toValueEffect();
			cardValueEffect.setBeginRound(cardValueEffect.getBeginRound() + 1);
			cardValueEffect.getTimesLimit().lostTimes();
			cardValueEffect.addExtraSkillEffect(getMySkillId());
			ar.addEffect(cardValueEffect);
		}
		psp.setReceiveEffect(null);
		int seq = psp.getNextAnimationSeq();
		AnimationSequence as = ClientAnimationService.getSkillAction(seq, getMySkillId(), psp.getPerformCard().getPos());
		ar.addClientAction(as);
		ar.setTakeEffect(true);
		return ar;
	}
}
