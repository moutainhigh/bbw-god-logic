package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * （3144）挑拨：每回合随机令敌方1张卡牌攻击另1张卡牌，若无可攻击目标，则正常攻击对位。（注：效果类似魅惑，可被心止防御，可以被金刚防御，不含云台）
 */
@Service
public class BattleSkill3144 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.TIAO_BO.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//每回合随机令敌方1张卡牌攻击另1张卡牌，若无可攻击目标，则正常攻击对位。
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		List<BattleCard> cards=psp.getOppoPlayingCards(false);
		List<BattleCard> targetCards=cards.stream().filter(p->!p.existSkillStatus(SKILL_ID)).collect(Collectors.toList());
		if (targetCards==null || targetCards.isEmpty()) {
			return ar;
		}
		BattleCard target = PowerRandom.getRandomFromList(targetCards);
		int sequence = psp.getNextAnimationSeq();
		BattleSkillEffect effect = getEffect(sequence, target, target.getPos());
		effect.setAttackPower(Effect.AttackPower.L1);
		ar.addEffect(effect);
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,int attkPos) {
		BattleCard myCard =fromPsp.getCombat().getBattleCard(attkPos);
		int sequence = fromPsp.getNextAnimationSeq();
		List<Effect> effects=new ArrayList<>();
		effects.add(getEffect(sequence, myCard, attkPos));
		return effects;
	}

	private BattleSkillEffect getEffect(int sequence,BattleCard myCard,int attkPos){
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, attkPos);
		//限制使用技能
		for (BattleSkill skill : myCard.get31013199Skills()) {
			TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
			effect.forbidOneRound(skill.getId(), limt,getMySkillId());
			effect.setSequence(sequence);
		}
		effect.changeSkillAttackTarget(CombatSkillEnum.NORMAL_ATTACK.getValue(), CombatSkillEnum.TIAO_BO.getValue());
		effect.setAttackPower(Effect.AttackPower.L2);
		return effect;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//每回合随机令敌方1张卡牌攻击另1张卡牌，若无可攻击目标，则正常攻击对位。
		int sequence = psp.getNextAnimationSeq();
		BattleSkillEffect effect = getEffect(sequence, target, target.getPos());
		effect.setAttackPower(Effect.AttackPower.L1);
		ar.addEffect(effect);
		return ar;
	}
}
