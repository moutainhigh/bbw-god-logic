package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 主角卡 上场才能获得经验丹
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-16 10:38
 */
@Service
public class BattleSkill1091 extends BattleSkillService {
	private static final int SKILL_ID = 1091;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		psp.getPerformPlayer().getStatistics().setGainJYD(true);
		return ar;
	}
}
