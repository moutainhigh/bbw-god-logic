package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 4203	嗜血	每次击中敌方卡牌后攻击永久+60，每升一阶增加40%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-18 23:12
 */
@Service
public class BattleSkill4203 extends BattleSkillService {
	private static final int SKILL_ID = 4203;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		if (!psp.getPerformCard().isHit()) {
			return ar;
		}
		//4203	嗜血	每次击中敌方卡牌后攻击永久+60，每升一阶增加40%的效果。
		CardValueEffect selfEffeck = CardValueEffect.getSkillEffect(CombatSkillEnum.SX.getValue(), psp.getPerformCard().getPos());
		Double roundAtk = 60 * psp.getPerformCard().getStars() * (1 + 0.4 * psp.getPerformCard().getHv());
		psp.getPerformCard().updateNormalAttackPreAtk(this.getInt(roundAtk));
		selfEffeck.setRoundAtk(this.getInt(roundAtk));
		selfEffeck.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(selfEffeck);
		return ar;
	}
}
