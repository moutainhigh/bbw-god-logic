package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 招财	如上场，战斗所得金钱多100%。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-16 10:38
 */
@Service
public class BattleSkill1090 extends BattleSkillService {
	private static final int SKILL_ID = 1090;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int times = psp.getPerformPlayer().getStatistics().getZhaoCaiEffectTimes();
		times++;
		psp.getPerformPlayer().getStatistics().setZhaoCaiEffectTimes(times);
		AnimationSequence as = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
		ar.addClientAction(as);
		ar.setTakeEffect(true);
		return ar;
	}
}
