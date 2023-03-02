package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 王者（玩家)	每回合从手牌中随机召唤同属性卡牌，填满我方阵型的空位。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 09:35
 */
@Service
public class BattleSkill3111 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.WZ.getValue();//技能ID
	@Autowired
	private BattleSkill1101 battleSkill1101;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = battleSkill1101.attack(psp);
		if (ListUtil.isNotEmpty(action.getClientActions())) {
			for (AnimationSequence clientAction : action.getClientActions()) {
				for (AnimationSequence.Animation animation : clientAction.getList()) {
					animation.setSkill(SKILL_ID);
				}
			}
		}
		return action;
	}
}
