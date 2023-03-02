package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 得道	下回合我方手牌中卡牌召唤所需法力值降低1。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 03:40
 */
@Service
public class BattleSkill3113 extends BattleSkillService {
	private static final int SKILL_ID = 3113;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//得道	下回合我方手牌中卡牌召唤所需法力值降低1。
		//生成1个动画即可
		int sequence = psp.getNextAnimationSeq();
		int mpAddtion = psp.getPerformPlayer().getStatistics().getHandCardRoundMpAddtion();
		mpAddtion -= 1;
		psp.getPerformPlayer().getStatistics().setHandCardRoundMpAddtion(mpAddtion);
		AnimationSequence animationSequence= ClientAnimationService.getSkillAction(sequence,SKILL_ID, psp.getPerformCard().getPos(), psp.getPerformCard().getPos());
		ar.addClientAction(animationSequence);
		return ar;
	}
}
