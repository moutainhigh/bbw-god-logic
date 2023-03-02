package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseService;
import org.springframework.stereotype.Service;

/**
 * 金葫芦	使用后，该回合魅惑、枷锁对我方卡牌无效。该回合一场战斗限用1次。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill8001 extends BattleSkillDefenseService {
	private static final int SKILL_ID = 8001;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
