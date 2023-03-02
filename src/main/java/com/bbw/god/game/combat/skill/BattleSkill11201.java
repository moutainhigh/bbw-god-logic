package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【绝影】：受到普通攻击时，有x%（受韧性影响，概率上限30%）概率避开本次攻击。
 *
 * @author: suhq
 * @date: 2022/9/24 2:10 下午
 */
@Service
public class BattleSkill11201 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.JUE_YING_WEAPON.getValue();
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return action;
		}
		Effect receiveEffect = psp.getReceiveEffect();
		if (CombatSkillEnum.NORMAL_ATTACK.getValue() != receiveEffect.getPerformSkillID()) {
			return action;
		}
		//概率命中，发动绝影技能，避开本次攻击
		action.getEffects().clear();
		action.setTakeEffect(true);
		psp.setReceiveEffect(null);
		//触发 补充一个动画
		AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos());
		action.addClientAction(amin);
		return action;
	}
}