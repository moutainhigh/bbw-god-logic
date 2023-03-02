package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.CardEquipmentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 【破天】：攻击召唤师时，有x%（受韧性影响，概率上限60%）概率增加x%（受强度影响，额外增加上限120%）攻击值。
 *
 * @author: suhq
 * @date: 2022/9/24 11:28 上午
 */
@Service
public class BattleSkill10102 extends BattleSkillService {
	@Autowired
	private CardEquipmentSkillService cardEquipmentSkillService;

	@Override
	public int getMySkillId() {
		return CombatSkillEnum.PO_TIAN_WEAPON.getValue();
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		BattleCard myCard = psp.getPerformCard();
		if (myCard.hasZuanDiSkill() && !psp.getOppoPlayingCards(false).isEmpty()) {
			// 卡牌含有钻地技能 且 不含云台的阵上卡不为空时 不发动突袭
			return action;
		}
		// 对位卡牌
		Optional<BattleCard> oppoCard = psp.getFaceToFaceCard();
		// 对位有卡牌,不触发技能
		if (oppoCard.isPresent()) {
			return action;
		}
		if (!cardEquipmentSkillService.isToPerform(psp.getPerformCard(), getMySkillId())) {
			return action;
		}
		double addRate = cardEquipmentSkillService.getExtraSkillRate(psp.getPerformCard(), getMySkillId());
		BattleCard sourceCard = psp.getPerformCard();
		CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), sourceCard.getPos());
		int atkAdd = (int) (sourceCard.getAtk() * addRate);
		effect.setAtk(atkAdd);
		action.addEffect(effect);
		return action;
	}
}
