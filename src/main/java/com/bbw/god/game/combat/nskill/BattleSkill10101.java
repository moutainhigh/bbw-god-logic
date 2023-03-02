package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 【斩魄】：攻击卡牌时，有x%（受韧性影响，概率上限60%）概率增加x%（受强度影响，额外增加上限100%）攻击值。
 *
 * @author: suhq
 * @date: 2022/9/24 11:25 上午
 */
@Service
public class BattleSkill10101 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.ZHAN_PO_WEAPON.getValue();
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		Optional<BattleCard> faceOptional = psp.getFaceToFaceCard();
		if (!faceOptional.isPresent() || faceOptional.get().isKilled()) {
			BattleCard myCard = psp.getPerformCard();
			if ((!myCard.hasZuanDiSkill() && !myCard.hasSkill(CombatSkillEnum.DJ.getValue()))
					|| psp.getOppoPlayingCards(false).isEmpty()) {
				return action;
			}
		}
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return action;
		}
		double addRate = cardEquipmentSkillService.getExtraSkillRate(psp.getPerformCard(), getMySkillId());
		BattleCard sourceCard = psp.getPerformCard();
		int addAtk = this.getInt(sourceCard.getAtk() * addRate);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), psp.getPerformCard().getPos());
		effect.setValueType(CardValueEffectType.DELAY);
		effect.setAtk(addAtk);
		action.addEffect(effect);
		return action;
	}
}
