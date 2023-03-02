package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 联攻	每有一张同属性卡牌在场，则该回合攻防各上升卡牌自身星级*50+级数*10。每升一阶增加15%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 01:55
 */
@Service
public class BattleSkill2201 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.LG.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//联攻	每有一张同属性卡牌在场，则该回合攻防各上升卡牌自身星级*50+级数*10。每升一阶增加15%的效果。
		BattleCard lgCard = psp.getPerformCard();
		List<BattleCard> playingCards = psp.getMyPlayingCards(true);
		//同属性的卡
		List<BattleCard> typeCards = playingCards.stream().filter(card -> card.getType() == lgCard.getType()).collect(Collectors.toList());
		if (typeCards.size() <= 1) {
			return ar;
		}
		double atk = (lgCard.getStars() * 50 + lgCard.getLv() * 10) * (1 + 0.15 * lgCard.getHv());
		atk = atk * (typeCards.size() - 1);
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, lgCard.getPos());
		effect.setAtk(this.getInt(atk));
		effect.setHp(this.getInt(atk));
		ar.addEffect(effect);
		return ar;
	}
}
