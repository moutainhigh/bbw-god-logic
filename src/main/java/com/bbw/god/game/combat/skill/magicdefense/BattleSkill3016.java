package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.AttackServiceFactory;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 神照 ID:3016 ：【封咒】、【禁术】、【蚀月】对其无效且反弹到施法卡牌。
 * @author lwb
 */
@Service
public class BattleSkill3016 implements ISkillDefenseService {
	private static final int SKILL_ID = 3016;
	@Autowired
	private AttackServiceFactory serviceFactory;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action action = new Action();
		//如果不是技能伤害，则不发动技能
		if (!psp.receiveSkillEffect()) {
			return action;
		}
		int[] defenseTableBySkillIds = BattleSkillDefenseTableService.getDefenseTableBySkillId(SKILL_ID);
		// 不在防守技能列表中
		if (!this.contains(defenseTableBySkillIds, psp.getReceiveEffectSkillId())) {
			return action;
		}
		//生成一个反弹自身的不可防御技能效果
		int targetPos = psp.getReceiveEffect().getSourcePos();
		int skillId = psp.getReceiveEffectSkillId();
		BattleSkillService service = serviceFactory.getSkillAttackService(skillId);
		psp.getReceiveEffect().setSourceID(skillId);
		List<Effect> effects = service.attackTargetPosByCopyEffects(psp, targetPos);
		for (Effect effect : effects) {
			effect.setPerformSkillID(skillId);
			effect.setSourceID(skillId);
		}
		action.addEffects(effects);
		//清除作用自身的技能效果
		psp.setReceiveEffect(null);
		return action;
	}
}
