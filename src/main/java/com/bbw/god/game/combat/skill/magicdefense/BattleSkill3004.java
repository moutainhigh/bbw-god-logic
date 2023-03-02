package com.bbw.god.game.combat.skill.magicdefense;

import org.springframework.stereotype.Service;

/**
 * 自在	枷锁技能对其无效。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill3004 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 3004;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

}
