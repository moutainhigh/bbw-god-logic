package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 威风 每回合敌方场上随机1张卡牌（不含云台）70%概率回到牌组，每阶提升3%成功率。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 00:43
 */
@Service
public class BattleSkill3107 extends BattleSkillService {
	private static final int SKILL_ID = 3107;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//威风	每回合敌方场上随机1张卡牌（不含云台）70%概率回到牌组。
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		int probability = 70 + 3 * card.getHv();
		if (!PowerRandom.hitProbability(probability)) {
			return ar;
		}
		//触发
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
		oppoPlayingCards=oppoPlayingCards.stream().filter(p->!p.hasSkill(CombatSkillEnum.SL.getValue())).collect(Collectors.toList());
		if (ListUtil.isEmpty(oppoPlayingCards)){
			return ar;
		}
		BattleCard targetCard = PowerRandom.getRandomFromList(oppoPlayingCards);
		return buildEffects(targetCard,psp);
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//威风	每回合敌方场上随机1张卡牌（不含云台）70%概率回到牌组。
		ar.setTakeEffect(true);
		//拉回牌堆
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, target.getPos());
		effect.moveTo(PositionType.DRAWCARD);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}
}
