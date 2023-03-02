package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 天雷 3172：每回合，减少敌方地面卡牌最低100点防御值，无视【回光】，每级增加50点最高值，每阶增加50%效果。
 *
 * @author: suhq
 * @date: 2022/8/26 10:57 上午
 */
@Service
public class BattleSkill3172 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.TIAN_LEI.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> targets = psp.getOppoPlayingCards(false);
		if (ListUtil.isEmpty(targets)) {
			return ar;
		}
		for (BattleCard target : targets) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
			double low = 100 * (1 + psp.getPerformCard().getHv() * 0.5);// 下限
			double hight = (100 + psp.getPerformCard().getLv() * 50) * (1 + psp.getPerformCard().getHv() * 0.5);// 上限
			int max = getInt(hight);
			int min = low > hight ? max : getInt(low);// 下限超过上限 则最小值就为上限值
			int hp = PowerRandom.getRandomBetween(min, max);
			effect.setHp(-hp);
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
		}

		return ar;
	}
}
