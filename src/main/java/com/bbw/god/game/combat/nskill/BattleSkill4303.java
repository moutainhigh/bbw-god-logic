package com.bbw.god.game.combat.nskill;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;

/**
 * 地劫：无视自己的站位，率先攻击敌方当前防御值最低的卡牌，若被攻击卡牌阵亡，则该卡无法发动死亡技能。
 * 
 * @author lwb
 * @version 1.0.0
 * @date 2020-02-19
 */
@Service
public class BattleSkill4303 extends BattleNormalAttack {
	private static final int SKILL_ID = 4303;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
		// 无视自己的站位，率先攻击敌方当前防御值最低的卡牌，若被攻击卡牌阵亡，则该卡无法发动死亡技能。
		Optional<BattleCard> minHp = oppoPlayingCards.stream().min(Comparator.comparing(BattleCard::getHp));
		if (!minHp.isPresent()) {
			return ar;
		}
		// 指定普攻的目标为 找到的最弱的卡牌
		BattleCard card = psp.getPerformCard();
		Optional<BattleSkill> match = card.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		match.get().setTargetPos(minHp.get().getPos(), SKILL_ID);
		getNormalAttackEffect(psp, ar);
		return ar;
	}
}
