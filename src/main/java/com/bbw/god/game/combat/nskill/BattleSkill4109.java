package com.bbw.god.game.combat.nskill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.TypeEnum;

/**
 * 水克火	面对属性克制卡牌，攻击力提高50%
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-15 16:05
 */
@Service
public class BattleSkill4109 extends RestrainBattleSkill {
	private static final int SKILL_ID = 4109;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	public TypeEnum getRestrainType() {
		return TypeEnum.Fire;
	}

}
