package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 暴刺 1020：上场时，施放1次【暴起】。自带【穿刺】。
 *
 * @author: suhq
 * @date: 2022/5/24 9:23 上午
 */
@Service
public class BattleSkill1020 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.BAO_CI.getValue();
	@Autowired
	private BattleSkill1012 battleSkill1012;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = battleSkill1012.attack(psp);
		if (!ar.existsEffect()) {
			return ar;
		}
		//修改触发效果的技能
		for (Effect effect : ar.getEffects()) {
			effect.setSourceID(SKILL_ID);
		}
		return ar;
	}
}
