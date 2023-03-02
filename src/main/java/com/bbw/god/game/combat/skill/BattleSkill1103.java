package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 哮天：自带【混元】、【暗蛊】、【禁术】技能。
 *
 */
@Service
public class BattleSkill1103 extends BattleSkillService {
	private static final int SKILL_ID = 1103;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		return new Action();
	}
}
