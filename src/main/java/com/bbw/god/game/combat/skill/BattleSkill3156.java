package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * 激励（3156）：每回合随机将我方场上随机一张卡牌攻防翻倍。(可能为自身)
 *
 *
 */
@Service
public class BattleSkill3156 extends BattleSkillService {
	private static final int SKILL_ID = 3156;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cardList=psp.getMyPlayingCards(true);
		if (ListUtil.isEmpty(cardList)){
			return ar;
		}
		BattleCard randomCard = PowerRandom.getRandomFromList(cardList);
		CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getMySkillId(),randomCard.getPos());
		valueEffect.setSequence(psp.getNextAnimationSeq());
		valueEffect.setHp(randomCard.getHp());
		valueEffect.setAtk(randomCard.getAtk());
		ar.addEffect(valueEffect);
		return ar;
	}
}
