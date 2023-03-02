package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 暴起（1012）：上场时，该回合攻击和防御各加50%。每升一阶增加5%的效果。（注：封神后上台、生长后上台，不算上场回合）
 * 
 */
@Service
public class BattleSkill1012 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.BAO_QI.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 上场时，该回合攻击和防御各加50%。每升一阶增加5%的效果。（注：封神后上台、生长后上台，不算上场回合）
		BattleCard targetCard = psp.getPerformCard();
		CardValueEffect cardValueEffect=CardValueEffect.getSkillEffect(SKILL_ID,targetCard.getPos());
		int hp=getInt(targetCard.getHp()*(0.5+0.05*targetCard.getHv()));
		int atk=getInt(targetCard.getAtk()*(0.5+0.05*targetCard.getHv()));
		cardValueEffect.setHp(hp);
		cardValueEffect.setAtk(atk);
		ar.addEffect(cardValueEffect);
		return ar;
	}
}
