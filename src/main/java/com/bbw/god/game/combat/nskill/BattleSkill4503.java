package com.bbw.god.game.combat.nskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 飞闪（4503）：此卡召唤的所需法术值为1，且受到普通攻击时，有65%的概率避开，且每升一阶增加2%的闪避率。
 */
@Service
public class BattleSkill4503 extends BattleSkillService {
	private static final int SKILL_ID =4503;//技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		ar.setTakeEffect(false);
		//没有收到伤害
		if (null == psp.getReceiveEffect()) {
			return ar;
		}
		//有65%的概率避开，
		int probability = 65+psp.getPerformCard().getHv()*2;
		//触发灵动
		if (PowerRandom.hitProbability(probability)) {
			//概率命中，发动灵动技能，删除物理伤害
			ar.getEffects().clear();
			ar.setTakeEffect(true);
			psp.setReceiveEffect(null);
			//触发 补充一个动画
			AnimationSequence amin=ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
			ar.addClientAction(amin);
		}
		return ar;
	}
}
