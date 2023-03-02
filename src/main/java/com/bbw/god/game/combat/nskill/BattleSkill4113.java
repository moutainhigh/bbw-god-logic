package com.bbw.god.game.combat.nskill;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 4404 死斗 当敌方卡牌攻击高于自身防御时，则自身攻击翻倍。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 05:14
 */
@Service
public class BattleSkill4113 extends BattleSkillService {
	private static final int SKILL_ID = 4113;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 对位卡牌
		Optional<BattleCard> oppoCard = psp.getFaceToFaceCard();
		if (!oppoCard.isPresent()) {
			return ar;
		}
		// 对位有卡牌,触发技能
		BattleCard targetCard = oppoCard.get();
		BattleCard sourceCard = psp.getPerformCard();
		// 死斗 当敌方卡牌攻击高于自身防御时，则自身攻击翻倍。
		if (targetCard.getAtk() >= sourceCard.getHp()) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, sourceCard.getPos());
			int atk = sourceCard.getAtk();
			effect.setAtk(atk);
			ar.addEffect(effect);
		}

		return ar;
	}
}
