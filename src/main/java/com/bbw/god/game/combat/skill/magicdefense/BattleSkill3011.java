package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 灵守（3011）：自身及左右两边受到的法术伤害值减半。（与法盾不叠加）
 */
@Service
public class BattleSkill3011 implements ISkillDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.LS.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	private List<Effect> doSkill(PerformSkillParam psp){
		List<Effect> effects=new ArrayList<>();
		CardValueEffect effect= CloneUtil.clone(psp.getReceiveEffect().toValueEffect());
		effect.addExtraSkillEffect(getMySkillId());
		if (effect.getHp()>0 || effect.getRoundHp()>0){
			//增益效果
			return effects;
		}
		if (effect.getHp()!=0){
			effect.setHp(effect.getHp()/2);
		}
		if (effect.getRoundHp()!=0){
			effect.setRoundHp(effect.getRoundHp()/2);
		}
		effects.add(effect);
		if (effect.getValueType().getType()== CardValueEffect.CardValueEffectType.LASTING.getType()){
			effect.setValueType(CardValueEffect.CardValueEffectType.IN_TIME);
			CardValueEffect cardValueEffect=psp.getReceiveEffect().toValueEffect();
			cardValueEffect.setBeginRound(cardValueEffect.getBeginRound()+1);
			cardValueEffect.getTimesLimit().lostTimes();
			effects.add(cardValueEffect);
		}
		psp.setReceiveEffect(null);
		return effects;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		ar.setTakeEffect(false);
		int pos=psp.getPerformCard().getPos()-psp.getReceiveEffect().getTargetPos();
		int interval=Math.abs(pos);
		if (interval>1){
			//非相连卡牌  不触发
			return ar;
		}
		//如果不是 给自己释放灵守，那么 释放者 和目标位置都不能是云台位（即云台是独立位置）
		if (interval!=0 &&( PositionService.isYunTaiPos(psp.getPerformCard().getPos()) || PositionService.isYunTaiPos(psp.getReceiveEffect().getTargetPos()))){
			return ar;
		}

		if (!canTakeDefense(psp.getReceiveEffect())){
			return ar;
		}
		int targetPos=psp.getReceiveEffect().getTargetPos();
		int receiveEffectSkillId=psp.getReceiveEffectSkillId();
		int seq=psp.getReceiveEffect().getSequence();
		List<Effect> effects=doSkill(psp);
		if (effects.isEmpty()){
			return ar;
		}
		ar.getEffects().clear();
		ar.addEffects(effects);
		if (receiveEffectSkillId!= RunesEnum.ZHNE_SHE.getRunesId() && receiveEffectSkillId!=RunesEnum.WEI_SHE.getRunesId()){
			AnimationSequence as= ClientAnimationService.getSkillAction(seq+1,getMySkillId(),targetPos);
			ar.addClientAction(as);
		}else {
			ar.setNeedAddAnimation(false);
		}
		ar.setTakeEffect(true);
		return ar;
	}

	private boolean canTakeDefense(Effect effect){
		if (effect==null){
			return false;
		}
		if (!effect.isValueEffect()){
			return false;
		}
		if (effect.hasExtraSkillEffect(CombatSkillEnum.FD.getValue())){
			return false;
		}
		if (effect.hasExtraSkillEffect(CombatSkillEnum.LS.getValue())){
			return false;
		}
		if (effect.hasExtraSkillEffect(CombatSkillEnum.NORMAL_ATTACK.getValue())){
			return false;
		}
		return true;
	}
}
