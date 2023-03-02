package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 食尸（3152）：每回合将敌方坟场中的每张卡牌攻防的5%，将加到该卡牌的攻防中。每升一阶提高1%的效果。
 *
 */
@Service
public class BattleSkill3152 extends BattleSkillService {
	private static final int SKILL_ID = 3152;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//每回合将敌方坟场中的每张卡牌攻防的5%，将加到该卡牌的攻防中。每升一阶提高1%的效果。
		List<BattleCard> discards = psp.getOppoPlayer().getDiscard();
		if (ListUtil.isEmpty(discards)){
			return ar;
		}
		int sumAtk=0;
		int sumHp=0;
		float multiple=0.05f+psp.getPerformCard().getHv()*0.01f;
		for (BattleCard card:discards){
			sumAtk+=card.getAtk()*multiple;
			sumHp+=card.getHp()*multiple;
		}
		CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getMySkillId(),psp.getPerformCard().getPos());
		valueEffect.setHp(sumHp);
		valueEffect.setAtk(sumAtk);
		ar.addEffect(valueEffect);
		return ar;
	}
}
