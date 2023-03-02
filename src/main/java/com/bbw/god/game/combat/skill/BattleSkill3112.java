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
 * 枷锁 每回合有70%概率随机封锁敌方战场上1张卡牌（不含云台），被封锁的卡牌无法攻击，无法使用主动技能，每阶提高3%的成功率
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 03:41
 */
@Service
public class BattleSkill3112 extends BattleSkillService {
	private static final int SKILL_ID = 3112;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//枷锁	每回合有70%概率随机封锁敌方战场上1张卡牌（不含云台），被封锁的卡牌无法攻击，无法使用主动技能。
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		int probability = 70 + 3 * card.getHv();
		//触发
		if (!PowerRandom.hitProbability(probability)) {
			return ar;
		}
		List<BattleCard> cards=psp.getOppoPlayingCards(false);
		List<BattleCard> targetCards=cards.stream().filter(p->!p.existSkillStatus(SKILL_ID) && !p.existSkillStatus(CombatSkillEnum.LIAN_SUO.getValue())).collect(Collectors.toList());
		if (targetCards==null || targetCards.isEmpty()) {
			return ar;
		}
		BattleCard target = PowerRandom.getRandomFromList(targetCards);
		BattleSkillEffect effect =getEffect(target);
		ar.addEffect(effect);
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,int attkPos) {
		List<Effect> effects=new ArrayList<Effect>();
		BattleCard targetCard = fromPsp.getCombat().getBattleCard(attkPos);
		BattleSkillEffect effect =getEffect(targetCard);
		effect.setAttackPower(AttackPower.L2);
		effects.add(effect);
		return effects;
	}

	public BattleSkillEffect getEffect(BattleCard targetCard) {
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		//锁定目标卡牌的技能
		for (BattleSkill skill : targetCard.getActiveAttackSkills()) {
			TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
			effect.forbidOneRound(skill.getId(), limt,getMySkillId());
		}
		effect.setAttackPower(AttackPower.L1);
		return effect;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//枷锁	每回合有70%概率随机封锁敌方战场上1张卡牌（不含云台），被封锁的卡牌无法攻击，无法使用主动技能。
		BattleSkillEffect effect =getEffect(target);
		ar.addEffect(effect);
		return ar;
	}
}
