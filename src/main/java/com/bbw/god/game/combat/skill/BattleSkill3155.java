package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * 弘法（3155）：每回合令我方场上与自身属性相同的卡牌增加20%的攻防，每升一阶再增加3%的攻防。（包括自身）
 *
 *
 */
@Service
public class BattleSkill3155 extends BattleSkillService {
	private static final int SKILL_ID = 3155;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int type=psp.getPerformCard().getType().getValue();
		List<BattleCard> cardList=psp.getMyPlayingCards(true).stream().filter(p->p.getType().getValue()==type).collect(Collectors.toList());
		int seq=psp.getNextAnimationSeq();
		double multiple=0.2+0.03*psp.getPerformCard().getHv();
		for (BattleCard card:cardList){
			CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getMySkillId(),card.getPos());
			valueEffect.setSequence(seq);
			valueEffect.setHp(getInt(card.getHp()*multiple));
			valueEffect.setAtk(getInt(card.getAtk()*multiple));
			ar.addEffect(valueEffect);
		}
		return ar;
	}
}
