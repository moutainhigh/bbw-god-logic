package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 奉神：每回合，优先于上场技能发动，将我方1张坟场卡牌置入战场。战场已满时，则回到卡组。
 * （1）该技能性质同AI王者，在上场技之前发动，具有最高优先级。
 * （2）最高优先级释放顺序：AI王者（1101）＞奉神（1105）＞祭鞭（1104）＞禁术（1102）。
 *
 * @author: suhq
 * @date: 2021/12/3 4:41 下午
 */
@Service
public class BattleSkill1105 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.FENG_SHEN.getValue();// 技能ID
	@Autowired
	private BattleSkill3104 battleSkill3104;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = battleSkill3104.buildAction(psp);
		return ar;
	}
}
