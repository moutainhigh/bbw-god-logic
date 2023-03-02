package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【蚀骨】：攻击时，有x%（受韧性影响，概率上限40%）概率在本回合获得【销魂】，一回合触发一次。
 *
 * @author: suhq
 * @date: 2022/9/24 2:18 下午
 */
@Service
public class BattleSkill10202 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.SHI_GU_WEAPON.getValue();
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return action;
		}
		// 获取本回合该技能释放次数
		int times = psp.getPerformCard().gainTimesOfSkillPerformInRound(getMySkillId(), psp.getCombat().getRound());
		if (times >= 1){
			// 判断该技能在本回合已经释放 ， 则跳过本次释放（一回合触发一次）
			return action;
		}
		int seq = psp.getNextAnimationSeq();
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getMySkillId(), psp.getPerformCard().getPos());
		effect.setSequence(seq);
		effect.addSkill(CombatSkillEnum.XH.getValue(), TimesLimit.oneTimeLimit());
		action.addEffect(effect);
		return action;
	}
}