package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 疾军：每回合，使我方下回合手牌上阵所需法力值为1。
 *
 * @author: suhq
 * @date: 2021/12/3 3:30 下午
 */
@Service
public class BattleSkill3170 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.JI_JUN.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		psp.getPerformPlayer().getStatistics().setNextRoundHandCardRoundMp(1);
		//生成1个动画即可
		int sequence = psp.getNextAnimationSeq();
		AnimationSequence animationSequence = ClientAnimationService.getSkillAction(sequence, getMySkillId(), psp.getPerformCard().getPos(), psp.getPerformCard().getPos());
		ar.addClientAction(animationSequence);
		return ar;
	}
}
