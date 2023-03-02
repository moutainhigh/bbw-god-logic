package com.bbw.god.game.combat.nskill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 物理攻击技能
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 11:14
 */
@Service
public class BattleSkill4401 extends BattleNormalAttack {
	private static final int SKILL_ID = 4401;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		// 军师位 普攻不加成
		return attack(psp);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		getNormalAttackEffect(psp, ar);
		return ar;
	}
}
