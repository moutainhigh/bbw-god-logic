package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 突袭 直接攻击召唤师时，攻击力提高80%，每升一阶提高5%的效果。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 05:14
 */
@Service
public class BattleSkill4112 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.TX.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		buildEffect(ar, psp);
		return ar;
	}

	/**
	 * 构建突袭的效果
	 *
	 * @param ar
	 * @param psp
	 */
	public void buildEffect(Action ar, PerformSkillParam psp) {
		BattleCard myCard = psp.getPerformCard();
		if (myCard.hasZuanDiSkill() && !psp.getOppoPlayingCards(false).isEmpty()) {
			// 卡牌含有钻地技能 且 不含云台的阵上卡不为空时 不发动突袭
			return;
		}
		// 对位卡牌
		Optional<BattleCard> oppoCard = psp.getFaceToFaceCard();
		// 对位没有卡牌,触发技能
		if (!oppoCard.isPresent()) {
			// 4103 突袭 直接攻击召唤师时，攻击力提高80%，每升一阶提高5%的效果。
			BattleCard sourceCard = psp.getPerformCard();
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, sourceCard.getPos());
			double atkAdd = sourceCard.getAtk() * (0.8 + sourceCard.getHv() * 0.05);
			int atk = this.getInt(atkAdd);
			effect.setAtk(atk);
			ar.addEffect(effect);
		}
	}
}
