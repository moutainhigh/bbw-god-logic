package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatConfig;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 王者（AI)	每回合从手牌中随机召唤同属性卡牌，填满我方阵型的空位。

 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 09:35
 */
@Service
public class BattleSkill1101 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.WZAI.getValue();//技能ID
	@Autowired
	private DeployCardsSolutionService upToService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//王者（AI)	每回合从手牌中随机召唤同属性卡牌，填满我方阵型的空位。
		List<BattleCard> myHandCards = psp.getMyHandCards();
		if (myHandCards.isEmpty()) {
			return ar;
		}
		//同属性的卡
		List<BattleCard> typeCards = myHandCards.stream().filter(card -> card.getType() == psp.getPerformCard().getType()).collect(Collectors.toList());
		if (typeCards.isEmpty()) {
			return ar;
		}
		int maxCardNum = CombatConfig.MAX_BATTLE_CARD - 1;
		List<CardMovement> movements = upToService.randomSolutionCardToBattle(psp.getCombat().getPlayer(psp.getPerformPlayerId()), typeCards, maxCardNum);

		if (movements.isEmpty()) {
			return ar;
		}
		int sequence=psp.getNextAnimationSeq();
		for (CardMovement move : movements) {
			CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, move.getFromPos());
			effect.moveTo(PositionType.BATTLE, move.getToPos());
			effect.setAttackPower(AttackPower.getMaxPower());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
			//王者效果动画是有次序的，而释放动画为同步的 所以这里手动生成动画
			AnimationSequence animationSequence=ClientAnimationService.getSkillAction(sequence, SKILL_ID, psp.getPerformCard().getPos(),move.getFromPos());
			ar.addClientAction(animationSequence);
		}
		return ar;
	}
}
