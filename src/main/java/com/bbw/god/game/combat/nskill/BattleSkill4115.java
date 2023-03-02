package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 销魂 每次攻击破除敌方永久防御。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-15 02:43
 */
@Service
public class BattleSkill4115 extends BattleSkillService {
	private static final int SKILL_ID = 4115;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 销魂 每次攻击破除敌方永久防御。
		CardValueEffect cardBuff = CardValueEffect.getSkillEffect(SKILL_ID, psp.getPerformCard().getPos());
		cardBuff.setValueType(CardValueEffectType.DELAY);
		ar.addEffect(cardBuff);
		return ar;
	}
}
