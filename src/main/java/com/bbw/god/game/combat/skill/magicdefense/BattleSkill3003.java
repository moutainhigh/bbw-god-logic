package com.bbw.god.game.combat.skill.magicdefense;

import org.springframework.stereotype.Service;

/**
 * 心止	魅惑技能对其无效。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill3003 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 3003;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
