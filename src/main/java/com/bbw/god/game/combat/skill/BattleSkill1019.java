package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatConfig;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元帅 1019：上场时，将手牌中与施法者同星级的卡牌置入战场，填满我方阵位。
 * （1）该技能的作用范围为我方卡组中的所有卡牌。
 * （2）不会将不具有【飞行】技能的卡牌置入云台位。
 *
 * @author: suhq
 * @date: 2022/1/17 2:18 下午
 */
@Service
public class BattleSkill1019 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.YUAN_SHUAI.getValue();
	@Autowired
	private DeployCardsSolutionService upToService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();

		List<BattleCard> myPlayingCards = psp.getMyPlayingCards(true);
		//场上的卡牌是否已满
		boolean isPlayingCardFull = myPlayingCards.size() == 6;
		if (isPlayingCardFull) {
			return ar;
		}
		List<BattleCard> myHandCards = psp.getMyHandCards();
//		List<BattleCard> myDrawCards = psp.getPerformPlayer().getDrawCards();
		//是否还有手牌
		if (myHandCards.isEmpty()) {
			return ar;
		}
		//同星级的卡
		List<BattleCard> starCards = new ArrayList<>();
		List<BattleCard> myHandStarCards = myHandCards.stream().filter(card -> card.getStars() == psp.getPerformCard().getStars()).collect(Collectors.toList());
//		List<BattleCard> myDrawStarCards = myDrawCards.stream().filter(card -> card.getStars() == psp.getPerformCard().getStars()).collect(Collectors.toList());
		starCards.addAll(myHandStarCards);
//		starCards.addAll(myDrawStarCards);
		if (starCards.isEmpty()) {
			return ar;
		}
		int maxCardNum = CombatConfig.MAX_BATTLE_CARD - 1;
		List<CardMovement> movements = upToService.randomSolutionCardToBattle(psp.getPerformPlayer(), starCards, maxCardNum);

		if (movements.isEmpty()) {
			return ar;
		}
		int sequence = psp.getNextAnimationSeq();
		for (CardMovement move : movements) {
			CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, move.getFromPos());
			effect.moveTo(PositionType.BATTLE, move.getToPos());
			effect.setAttackPower(Effect.AttackPower.getMaxPower());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
			//王者效果动画是有次序的，而释放动画为同步的 所以这里手动生成动画
			AnimationSequence animationSequence = ClientAnimationService.getSkillAction(sequence, SKILL_ID, psp.getPerformCard().getPos(), move.getFromPos());
			ar.addClientAction(animationSequence);
		}
		return ar;
	}
}
