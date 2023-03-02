package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 4302 钻地 可以无视自己的对位，率先攻击敌方当前防御最弱的卡牌。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 11:14
 */
@Service
public class BattleSkill4302 extends BattleNormalAttack {
	private static final int SKILL_ID = CombatSkillEnum.ZD.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
		// 4302 钻地 可以无视自己的对位，率先攻击敌方当前防御最弱的卡牌。
		Optional<BattleCard> minHp = oppoPlayingCards.stream().min(Comparator.comparing(BattleCard::getHp));
		if (!minHp.isPresent()) {
			return ar;
		}
		// 指定普攻的目标为 找到的最弱的卡牌
		BattleCard card = psp.getPerformCard();
		Optional<BattleSkill> match = card.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		match.get().setTargetPos(minHp.get().getPos());
		getNormalAttackEffect(psp, ar);
		return ar;
	}
}
