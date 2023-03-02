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
 * 3106 魅惑 每回合有70%概率令敌方场上随机1张卡牌（不含云台）攻击敌方召唤师，每阶提升3%成功率。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 00:43
 */
@Service
public class BattleSkill3106 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.MH.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		//3106	魅惑	每回合有70%概率令敌方场上随机1张卡牌（不含云台）攻击敌方召唤师。
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return new Action();
		}
		int probability = 70 + 3 * card.getHv();
		//没有触发技能
		if (!PowerRandom.hitProbability(probability)) {
			return new Action();
		}
		List<BattleCard> cards = psp.getOppoPlayingCards(false);
		List<BattleCard> targetCards = cards.stream()
				.filter(p -> !p.existSkillStatus(SKILL_ID) && !p.existSkillStatus(CombatSkillEnum.QING_GUO.getValue()))
				.collect(Collectors.toList());
		if (targetCards == null || targetCards.isEmpty()) {
			return new Action();
		}
		BattleCard target = PowerRandom.getRandomFromList(targetCards);
		return buildEffects(target, psp);
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,int attkPos) {
		int ZhsPos=fromPsp.getOppoZhsPos();
		BattleCard myCard =fromPsp.getCombat().getBattleCard(attkPos); 
		int sequence = fromPsp.getNextAnimationSeq();
		List<Effect> effects=new ArrayList<>();
		effects.add(getEffect(sequence, myCard, attkPos, ZhsPos));
		return effects;
	}

	public BattleSkillEffect getEffect(int sequence,BattleCard myCard,int attkPos,int ZhsPos){
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, attkPos);
		//指定物理攻击目标
		effect.changeSkillAttackTarget(CombatSkillEnum.NORMAL_ATTACK.getValue(), ZhsPos);
		//限制使用技能
		for (BattleSkill skill : myCard.get31013199Skills()) {
			TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
			effect.forbidOneRound(skill.getId(), limt,getMySkillId());
			effect.setSequence(sequence);
		}
		effect.setAttackPower(AttackPower.L2);
		return effect;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//3106	魅惑	每回合有70%概率令敌方场上随机1张卡牌（不含云台）攻击敌方召唤师。
		int oppoZhsPos = psp.getOppoZhsPos();
		int sequence = psp.getNextAnimationSeq();
		BattleSkillEffect effect = getEffect(sequence, target, target.getPos(), oppoZhsPos);
		effect.setAttackPower(AttackPower.L1);
		ar.addEffect(effect);
		return ar;
	}
}
