package com.bbw.god.game.combat.nskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 灵动。60%的概率避开敌方的攻击。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 10:25
 */
@Service
public class BattleSkill4501 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.LINGD.getValue();//技能ID
	//克制灵动的技能ID 4105神剑
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		ar.setTakeEffect(false);
		//没有收到伤害
		if (null == psp.getReceiveEffect()) {
			return ar;
		}
		//获取不到伤害来源卡
		Optional<BattleCard> sourceCard = psp.getEffectSourceCard();
		if (!sourceCard.isPresent()) {
			return ar;
		}
		// 带有神剑BUFF 或者是芒刺技能
		if (sourceCard.get().hasEffect(CombatSkillEnum.SHENJ) || psp.getReceiveEffectSkillId() == CombatSkillEnum.MANG_CI.getValue()) {
			return ar;
		}

		Optional<BattleSkill> skill = sourceCard.get().getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		if (skill.isPresent() &&( skill.get().changeTargetPosFromSkill(CombatSkillEnum.DJ.getValue()))) {
			return ar;
		}

		//灵动。60%的概率避开敌方的攻击。(每升一阶+2%)
		BattleCard performCard = psp.getPerformCard();
		int probability = 60 + performCard.getHv() * 2;
		//触发灵动
		if (PowerRandom.hitProbability(probability)) {
			//概率命中，发动灵动技能，删除物理伤害
			ar.getEffects().clear();
			ar.setTakeEffect(true);
			psp.setReceiveEffect(null);
			//触发 补充一个动画
			AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
			ar.addClientAction(amin);
		}
		//下一个来处理
		return ar;
	}
}
