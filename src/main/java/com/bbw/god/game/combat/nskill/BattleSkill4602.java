package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.BattleSkill3302;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 隐藏怒意 4602：受到普通攻击时，提高星级*120点永久攻击值，每阶增加40%效果。
 * （1）若卡牌在受到普通攻击导致该卡牌死亡或离场，则不会触发该技能。
 *
 * @author: suhq
 * @date: 2022/5/12 4:31 下午
 */
@Service
public class BattleSkill4602 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.NU_YI_HIDE.getValue();

	@Autowired
	private BattleSkill3302 battleSkill3204;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		return battleSkill3204.attack(psp);
	}
}
