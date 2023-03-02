package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;

import com.bbw.god.game.combat.data.CombatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;

/**
 * <pre>
 * 九龙岛四圣	两人在场，每回合随机拉一张手牌上场。三人以上在场，每回合随机拉手牌将阵型填满。
 * </pre>  
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2009 extends GroupSkillService {
	private static final int GROUP_ID = 2009;//组合ID
	private static final int MINIMUM_CARDS = 2;//至少需要多少张才能形成组合
	@Autowired
	private DeployCardsSolutionService setCardToBattleService;

	@Override
	public boolean match(int groupId) {
		return GROUP_ID == groupId;
	}

	@Override
	protected int getMinimumCards() {
		return MINIMUM_CARDS;
	}

	@Override
	protected List<Effect> groupAttack(Combat combat, PlayerId playing, List<BattleCard> groupCards) {

		//九龙岛四圣	两人在场，每回合随机拉一张手牌上场。三人以上在场，每回合随机拉手牌将阵型填满。
		//3人以上在场，拉满，否则拉1个人
		int maxCardNum = groupCards.size() >= 3 ? CombatConfig.MAX_BATTLE_CARD - 3 : 1;
		List<BattleCard> myHandCards = combat.getHandCards(playing);//我的上阵卡牌

		List<CardMovement> movements = setCardToBattleService.randomSolutionCardToBattle(combat.getPlayer(playing), myHandCards, maxCardNum);
		if (movements.isEmpty()) {
			return new ArrayList<>(0);
		}
		List<Effect> atks = new ArrayList<>(movements.size());
		int seq = combat.getAnimationSeq();
		for (CardMovement move : movements) {
			//生成移动对象
			CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(GROUP_ID, move.getFromPos());
			effect.setTargetPos(move.getFromPos());
			effect.moveTo(PositionType.BATTLE, move.getToPos());
			effect.setAttackPower(Effect.AttackPower.getMaxPower());
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}

}
