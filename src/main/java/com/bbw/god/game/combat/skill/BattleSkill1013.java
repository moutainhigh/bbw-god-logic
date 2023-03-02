package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 *暗蛊：上场时，令对位卡牌攻防减半，无视金刚。
 * 
 */
@Service
public class BattleSkill1013 extends BattleSkillService {
	private static final int SKILL_ID = 1013;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		if (!psp.getFaceToFaceCard().isPresent()){
			return ar;
		}
		BattleCard targetCard = psp.getFaceToFaceCard().get();
		CardValueEffect cardValueEffect=CardValueEffect.getSkillEffect(SKILL_ID,targetCard.getPos());
		cardValueEffect.setPerformSkillID(SKILL_ID);
		cardValueEffect.setRoundHp(-getInt(targetCard.getRoundHp()*0.5));
		cardValueEffect.setRoundAtk(-getInt(targetCard.getRoundAtk()*0.5));
		ar.addEffect(cardValueEffect);
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(), psp.getPerformCard().getPos(),targetCard.getPos()));
		return ar;
	}
}
