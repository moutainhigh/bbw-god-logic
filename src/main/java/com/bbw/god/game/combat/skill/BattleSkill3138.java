package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 连锁：② 每回合有70%概率随机封锁敌方战场上2张卡牌（不含云台），无视回光。被封锁的卡牌无法攻击，无法使用主动技能，每阶提高3%的发动率。
 * 
 * @author lwb
 * @date 2020年02月19日
 * @version 1.0
 */
@Service
public class BattleSkill3138 extends BattleSkillService {
	private static final int SKILL_ID = 3138;// 技能ID
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
		int probability = 70 + 3 * card.getHv();
		// 触发
		if (!PowerRandom.hitProbability(probability)) {
			return ar;
		}
		List<BattleCard> cards=psp.getOppoPlayingCards(false);
		List<BattleCard> targetCards=cards.stream().filter(p->!p.existSkillStatus(SKILL_ID) && !p.existSkillStatus(CombatSkillEnum.JS.getValue())).collect(Collectors.toList());
		List<BattleCard> targets =PowerRandom.getRandomsFromList(2,targetCards);
		if (targets.isEmpty()) {
			return ar;
		}
		int sequence = psp.getNextAnimationSeq();
		for (BattleCard targetCard : targets) {
			BattleSkillEffect effect = getEffect(targetCard);
			effect.setAttackPower(AttackPower.L1);
			effect.setSequence(sequence);
			ar.addEffect(effect);
		}
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp, int attkPos) {
		List<Effect> effects = new ArrayList<Effect>();
		BattleCard targetCard = fromPsp.getCombat().getBattleCard(attkPos);
		BattleSkillEffect effect = getEffect(targetCard);
		effects.add(effect);
		return effects;
	}

	private BattleSkillEffect getEffect(BattleCard targetCard) {
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		// 锁定目标卡牌的技能
		for (BattleSkill skill : targetCard.getActiveAttackSkills()) {
			TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
			effect.forbidOneRound(skill.getId(), limt,getMySkillId());
		}
		effect.setAttackPower(AttackPower.L2);
		return effect;
	}
}
