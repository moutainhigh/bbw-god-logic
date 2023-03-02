package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 禁术：上场时，禁用当前对位卡牌的所有技能。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-16 10:38
 */
@Service
public class BattleSkill1102 extends BattleSkillService {
	private static final int SKILL_ID = 1102;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Optional<BattleCard> faceOp = psp.getFaceToFaceCard();
		if (!faceOp.isPresent()) {
			return new Action();
		}
		return buildEffects(faceOp.get(),psp);
	}

	@Override
	public Action buildEffects(BattleCard targetCard, PerformSkillParam fromPsp){
		return buildEffects(targetCard, fromPsp,false);
	}

	public Action buildEffects(BattleCard targetCard, PerformSkillParam fromPsp,boolean includeSelf){
		Action ar = new Action();
		if (!includeSelf && targetCard.existSkill(getMySkillId()) ){
			return ar;
		}
		List<BattleSkill> skills = targetCard.getSkills();
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		effect.setLastRound(Integer.MAX_VALUE);
		for (BattleSkill skill : skills) {
			effect.forbid(skill,getMySkillId());
		}
		ar.addEffect(effect);
		ar.addClientAction(ClientAnimationService.getSkillAction(fromPsp.getNextAnimationSeq(),getMySkillId(),fromPsp.getPerformCard().getPos(),targetCard.getPos()));
		return ar;
	}
	/**
	 * 对目标位置 释放 禁术
	 * @return
	 */
	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp, int attkPos) {
		List<Effect> effects = new ArrayList<>();
		BattleCard battleCard = fromPsp.getCombat().getBattleCardByPos(attkPos);
		if (null == battleCard) {
			// 说明目标卡牌已死亡 或者 已被其他位置的反弹技能致死了
			return effects;
		}
		Action action = buildEffects(battleCard, fromPsp,true);
		effects.addAll(action.getEffects());
		for (Effect effect : effects) {
			effect.setSequence(fromPsp.getNextAnimationSeq());
		}
		return effects;
	}
}
