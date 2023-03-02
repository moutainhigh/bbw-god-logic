package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseService;
import org.springframework.stereotype.Service;

/**
 * 定神丹	使用后，该回合威风、斥退、妖术对我方卡牌无效。一场战斗限用1次。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill8002 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 8002;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
