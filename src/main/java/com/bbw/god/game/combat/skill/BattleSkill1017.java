package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 神医：上场时，对我方全体卡牌施放【巫医】。
 *
 * @author: suhq
 * @date: 2021/12/3 3:02 下午
 */
@Service
public class BattleSkill1017 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.SHEN_YI.getValue();// 技能ID
	@Autowired
	private BattleSkill3141 battleSkill3141;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		List<BattleCard> cards = psp.getMyPlayingCards(true);
		if (ListUtil.isEmpty(cards)) {
			return ar;
		}
		int seq = psp.getNextAnimationSeq();
		//每回合随机回复1张卡牌受到的永久伤害（恢复到卡牌初始攻防*阵位加成），5阶回复受到的全部伤害（恢复到存活期间最高永久血量）。10阶回复所有攻防（恢复到存活期间最高的攻防）。
		int hv = card.getHv();
		if (hv < 5) {
			for (BattleCard battleCard : cards) {
				battleSkill3141.attackHv(battleCard, seq, ar);
			}
		} else if (hv < 10) {
			for (BattleCard battleCard : cards) {
				battleSkill3141.attack5Hv(battleCard, seq, ar);
			}
		} else {
			for (BattleCard battleCard : cards) {
				battleSkill3141.attack10Hv(battleCard, seq, ar);
			}
		}
		return ar;
	}
}
