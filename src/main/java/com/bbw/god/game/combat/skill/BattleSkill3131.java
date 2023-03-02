package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 3131	封咒	每回合永久封禁对位卡牌的所有非上场技能。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:07
 */
@Service
public class
BattleSkill3131 extends BattleSkillService {
	private static final int SKILL_ID = 3131;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//	封咒	每回合永久封禁对位卡牌的所有非上场技能。(不含普攻)
		Optional<BattleCard> targetCardOp = psp.getFaceToFaceCard();
		if (!targetCardOp.isPresent()) {
			return ar;
		}
		return buildEffects(targetCardOp.get(),psp);
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		return buildEffects(target, psp,false);
	}

	public Action buildEffects(BattleCard target, PerformSkillParam psp,boolean includeSelf) {
		Action ar = new Action();
		//	封咒	每回合永久封禁对位卡牌的所有非上场技能。(不含普攻)
		if (target.getSkills().isEmpty()) {
			//对方卡牌技能为空，则只生成释放动画
			AnimationSequence anim= ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos(), target.getPos());
			ar.addClientAction(anim);
			return ar;
		}
		if (!includeSelf && target.existSkill(getMySkillId())){
			return ar;
		}
		//上场技能
		SkillSection upSkillSection = SkillSection.getDeploySection();
		int seq = psp.getCombat().getAnimationSeq();
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, target.getPos());
		effect.setLastRound(Integer.MAX_VALUE);
		effect.setSequence(seq);
		for (BattleSkill skill : target.getSkills()) {
			if (upSkillSection.contains(skill.getId())) {
				continue;
			}
			effect.forbid(skill,getMySkillId());
		}
		if (!effect.getEffectLimits().isEmpty()) {
			ar.addEffect(effect);
		}
		return ar;
	}

	/**
	 * 对目标位置 释放 封咒
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
