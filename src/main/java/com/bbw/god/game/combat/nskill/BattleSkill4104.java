package com.bbw.god.game.combat.nskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 4104	暴击	战斗中有70%的概率增加50%的攻击力，每升一阶增加10%的提升攻击力。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 05:14
 */
@Service
public class BattleSkill4104 extends BattleSkillService {
	private static final int SKILL_ID = 4104;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//4104	暴击	战斗中有70%的概率增加50%的攻击力，每升一阶增加10%的提升攻击力。
		 Optional<BattleCard>  faceOptional=  psp.getFaceToFaceCard();
		if (!faceOptional.isPresent() || faceOptional.get().isKilled()) {
			BattleCard myCard = psp.getPerformCard();
			if ((!myCard.hasZuanDiSkill() && !myCard.hasSkill(CombatSkillEnum.DJ.getValue()))
					|| psp.getOppoPlayingCards(false).isEmpty()) {
				return ar;
			}
		}
		int probability = 100;
		if (PowerRandom.hitProbability(probability)) {
			BattleCard sourceCard = psp.getPerformCard();
			int atk = this.getInt(sourceCard.getAtk()*(0.5 + 0.1 * sourceCard.getHv()));
			CardValueEffect effect=CardValueEffect.getSkillEffect(SKILL_ID, psp.getPerformCard().getPos());
			effect.setValueType(CardValueEffectType.DELAY);
			effect.setAtk(atk);
			ar.addEffect(effect);
		}
		return ar;
	}
}
