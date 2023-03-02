package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 物理击中
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-15 02:43
 */
@Service
public class BattleSkill4599 extends BattleSkillService {
	private static final int SKILL_ID = 4599;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	
	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return attack(psp);
	}


	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = psp.getDefenseAction();
		//没有收到属性伤害
		Effect effect = psp.getReceiveEffect();
		if (null == effect || effect.getResultType() != EffectResultType.CARD_VALUE_CHANGE) {
			return ar;
		}
		//被打中了
		psp.getPerformCard().setBehit(true);
		//攻击来源卡
		Optional<BattleCard> optional = psp.getEffectSourceCard();
		if (optional.isPresent()) {
			BattleCard sourceCard=optional.get();
			sourceCard.setHit(true);
			CardValueEffect receiveEffeck = psp.getReceiveEffect().toValueEffect();
			//攻击大于防御，则产生穿刺
			int total = receiveEffeck.getHp() + receiveEffeck.getRoundHp();
			int leftAtk=total + psp.getPerformCard().getHp();
			if (leftAtk < 0) {
				sourceCard.setLeftAtk(-leftAtk+sourceCard.getLeftAtk());
			}
		}
		return ar;
	}
}
