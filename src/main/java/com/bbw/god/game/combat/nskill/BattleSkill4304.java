package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 潜龙：自带【地劫】、【龙息】技能。
 * @author lwb
 * @version 1.0.0
 */
@Service
public class BattleSkill4304 extends BattleNormalAttack {
	private static final int SKILL_ID = 4304;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		return new Action();
	}
}
