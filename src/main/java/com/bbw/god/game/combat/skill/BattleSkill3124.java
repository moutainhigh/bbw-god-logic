package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 流毒	每回合随机指定等同于自身星级数量的敌方卡牌（不含云台），破除其60点永久防御，每升一阶增长50%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:44
 */
@Service
public class BattleSkill3124 extends BattleSkillService {
	private static final int SKILL_ID = 3124;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return generateAction(psp, 100);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		return generateAction(psp, 60);
	}

	private Action generateAction(PerformSkillParam psp, int baseRoundHp) {
		Action ar = new Action();
		//流毒	每回合随机指定等同于自身星级数量的敌方卡牌（不含云台），破除其60点永久防御，每升一阶增长50%的效果。
		BattleCard sourceCard = psp.getPerformCard();
		List<BattleCard> oppoPlayingCards = psp.randomOppoPlayingCards(sourceCard.getStars(), false);
		int roundHp = baseRoundHp;
		roundHp = this.getInt(roundHp * (1 + 0.5 * sourceCard.getHv()));
		int sequence = psp.getNextAnimationSeq();
		for (BattleCard card : oppoPlayingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setRoundHp(-roundHp);
			effect.setSequence(sequence);
			ar.addEffect(effect);
		}
		return ar;
	}
}
