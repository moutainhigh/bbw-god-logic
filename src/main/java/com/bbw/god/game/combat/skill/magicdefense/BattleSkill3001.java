package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import org.springframework.stereotype.Service;

/**
 * 金刚：免疫所有非永久效果的负面技能。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill3001 extends BattleSkillDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.JG.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
