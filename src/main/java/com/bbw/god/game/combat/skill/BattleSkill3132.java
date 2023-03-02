package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 *  蛊惑：下回合敌方法力值上限-1且所有卡牌召唤所需法力值+1。
 * @author Lwb
 *
 */
@Service
public class BattleSkill3132 extends BattleSkillService {
	private static final int SKILL_ID = 3132;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int sequence = psp.getNextAnimationSeq();
		int mpAddtion = psp.getOppoPlayer().getStatistics().getHandCardRoundMpAddtion();
		mpAddtion += 1;
		psp.getOppoPlayer().getStatistics().setHandCardRoundMpAddtion(mpAddtion);
		int mp = psp.getOppoPlayer().getStatistics().getMp();
		psp.getOppoPlayer().getStatistics().setMp(mp - 1);
		AnimationSequence animationSequence= ClientAnimationService.getSkillAction(sequence,SKILL_ID, psp.getPerformCard().getPos(), psp.getPerformCard().getPos());
		ar.addClientAction(animationSequence);
		return ar;
	}
}
