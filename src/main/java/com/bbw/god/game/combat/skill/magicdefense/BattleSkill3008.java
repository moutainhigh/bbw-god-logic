package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.game.combat.data.attack.Effect;
import org.springframework.stereotype.Service;

/**
 * 3008	返照	回光技能对其无效。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill3008 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 3008;//技能ID
	@Override
    public Effect.AttackPower getDefensePower() {
		return Effect.AttackPower.L3;
	}
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
