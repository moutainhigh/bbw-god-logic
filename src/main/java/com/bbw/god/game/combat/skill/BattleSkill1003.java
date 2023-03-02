package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 1003 上场时，随机使1张手牌无需消耗直接上场，每升一阶有6%的概率多使一张手牌上场。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:07
 */
@Service
public class BattleSkill1003 extends BattleSkillService {
	private static final int SKILL_ID = 1003;//技能ID
	@Autowired
	private DeployCardsSolutionService upToService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 1003 主帅 上场时随机使1张手牌无需消耗法术值上场,每升一阶有6%的概率多使一张手牌上场。
		List<BattleCard> myHandCards = psp.getMyHandCards();
		//没有手牌，或者战场已满
		if (myHandCards.isEmpty()) {
			return ar;
		}
		int maxCardNum = 1;
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		int probability = 6 * card.getHv();
		// 触发
		if (PowerRandom.hitProbability(probability)) {
			maxCardNum = 2;
		}
		int empty = psp.getPerformPlayer().getEmptyBattlePos(true).length;
		if (empty > 0 && empty < maxCardNum) {
			// 空位只有1个时
			maxCardNum = 1;
		} else if (empty == 0) {
			// 没有空位不发动技能
			return ar;
		}
		List<CardMovement> movements = upToService.randomSolutionCardToBattle(psp.getCombat().getPlayer(psp.getPerformPlayerId()), myHandCards, maxCardNum);
		if (movements.isEmpty()) {
			return ar;
		}
		int seq=psp.getNextAnimationSeq();
		for (CardMovement move : movements) {
			CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, move.getFromPos());
			effect.moveTo(PositionType.BATTLE, move.getToPos());
			effect.setAttackPower(AttackPower.getMaxPower());
			effect.setSequence(seq);
			ar.addEffect(effect);
		}
		AnimationSequence as=ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),SKILL_ID, card.getPos());
		ar.addClientAction(as);
		return ar;
	}
}
