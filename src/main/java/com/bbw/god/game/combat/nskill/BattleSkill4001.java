package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.skill.service.ISkillNormalBuffDefenseService;
import org.springframework.stereotype.Service;

/**
 * 无相。面对属性克制卡牌和暴击技能无视其加成效果。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 10:25
 */
@Service
public class BattleSkill4001 implements ISkillNormalBuffDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.WX.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
}
