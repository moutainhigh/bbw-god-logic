package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.stereotype.Service;

/**
 * 法盾（3010）：自身受到的法术伤害减半。
 */
@Service
public class BattleSkill3010 implements ISkillDefenseService {
	private static final int SKILL_ID = 3010;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		ar.setTakeEffect(false);
		if (psp.getReceiveEffect()==null){
			return ar;
		}
		if (!psp.getReceiveEffect().isValueEffect()  || psp.getReceiveEffect().hasExtraSkillEffect(CombatSkillEnum.LS.getValue())){
			return ar;
		}
		CardValueEffect effect= CloneUtil.clone(psp.getReceiveEffect().toValueEffect());
		effect.addExtraSkillEffect(getMySkillId());
		if (effect.getHp()>0 || effect.getRoundHp()>0){
			//增益效果
			return ar;
		}
		ar.getEffects().clear();
		if (effect.getHp()!=0){
			effect.setHp(effect.getHp()/2);
		}
		if (effect.getRoundHp()!=0){
			effect.setRoundHp(effect.getRoundHp()/2);
		}
		ar.addEffect(effect);
		if (effect.getValueType().getType()== CardValueEffect.CardValueEffectType.LASTING.getType()){
			effect.setValueType(CardValueEffect.CardValueEffectType.IN_TIME);
			CardValueEffect cardValueEffect=psp.getReceiveEffect().toValueEffect();
			cardValueEffect.setBeginRound(cardValueEffect.getBeginRound()+1);
			cardValueEffect.getTimesLimit().lostTimes();
			cardValueEffect.addExtraSkillEffect(getMySkillId());
			ar.addEffect(cardValueEffect);
		}
		psp.setReceiveEffect(null);
		int seq=psp.getNextAnimationSeq();
		AnimationSequence as= ClientAnimationService.getSkillAction(seq,getMySkillId(),psp.getPerformCard().getPos());
		ar.addClientAction(as);
		ar.setTakeEffect(true);
		return ar;
	}
}
