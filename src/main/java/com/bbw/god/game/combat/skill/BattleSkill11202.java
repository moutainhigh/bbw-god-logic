package com.bbw.god.game.combat.skill;

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
 * 【刺胄】：受到普通攻击时，有x%（受韧性影响，概率40%）概率反弹受到的100%普通攻击伤害。
 *
 * @author: suhq
 * @date: 2022/9/24 2:16 下午
 */
@Service
public class BattleSkill11202 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.CI_ZHOU_WEAPON.getValue();
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
		CardValueEffect cardValueEffect = receiveEffect.toValueEffect();
		int hp=cardValueEffect.getHp()+cardValueEffect.getRoundHp();
		if (hp>=0){
			return action;
		}
		CardValueEffect effect = CardValueEffect.getSkillEffect(CombatSkillEnum.CI_ZHOU_WEAPON.getValue(),cardValueEffect.getSourcePos());
		hp=Math.min(-hp,psp.getPerformCard().getHp());
		effect.setHp(-hp);
		action.addEffect(effect);
		//触发 补充一个动画
		AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos());
		action.addClientAction(amin);
		return action;
	}
}