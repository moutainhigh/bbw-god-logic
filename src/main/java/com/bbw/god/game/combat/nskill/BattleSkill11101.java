package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【鼋甲】：受到普通攻击时，有x%（受韧性影响，概率上限60%）概率减少受到的x%（受强度影响，减少伤害上限60%）伤害。
 *
 * @author: suhq
 * @date: 2022/9/24 11:41 上午
 */
@Service
public class BattleSkill11101 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.YUAN_JIA_WEAPON.getValue();
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		Effect receiveEffect = psp.getReceiveEffect();
		int skillId = psp.getReceiveEffect().getPerformSkillID();
		if (CombatSkillEnum.NORMAL_ATTACK.getValue() != skillId) {
			return ar;
		}
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return ar;
		}
		double deductRate = cardEquipmentSkillService.getExtraSkillRate(psp.getPerformCard(), getMySkillId());
		CardValueEffect valueEffect = receiveEffect.toValueEffect();
		valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductRate)));
		//触发 补充一个动画
		AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos());
		ar.addClientAction(amin);
		return ar;
	}
}
