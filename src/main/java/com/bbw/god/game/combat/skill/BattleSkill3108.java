package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 闪电 每回破除敌方随机1张卡牌（不含云台）最低100点防御，每级增加最高值上限50点，每阶增加50%效果。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 02:45
 */
@Service
public class BattleSkill3108 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.SD.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 闪电	每回破除敌方随机1张卡牌（不含云台）最低100点防御，每阶增加50%效果及最低值上限50点，每级增加最高值上限50点。
		Optional<BattleCard> rndOppoCard = psp.randomOppoPlayingCard(false);
		if (!rndOppoCard.isPresent()) {
			return ar;
		}
		BattleCard targetCard = rndOppoCard.get();
		return buildEffects(targetCard,psp);
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		// 闪电	每回破除敌方随机1张卡牌（不含云台）最低100点防御，每阶增加50%效果及最低值上限50点，每级增加最高值上限50点。
		//攻击目标卡牌
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
		double low = 100 * (1 + psp.getPerformCard().getHv() * 0.5);// 下限
		double hight = (100 + psp.getPerformCard().getLv() * 50) * (1 + psp.getPerformCard().getHv() * 0.5);// 上限
		int max = getInt(hight);
		int min = low > hight ? max : getInt(low);// 下限超过上限 则最小值就为上限值
		int hp = PowerRandom.getRandomBetween(min, max);
		effect.setHp(-hp);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
