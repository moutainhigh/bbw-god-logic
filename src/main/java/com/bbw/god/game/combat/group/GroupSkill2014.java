package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;

/**
 * <pre>
 * <font color=red>殷洪四天君</font>
 * 殷洪四天君	三人以上在场，每回合移除敌方一张墓地牌。四名以上在场，每回合移除敌方两张坟场卡。
 * </pre>
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2014 extends GroupSkillService {
	private static final int GROUP_ID = 2014;// 组合ID
	private static final int MINIMUM_CARDS = 3;// 至少需要多少张才能形成组合

	@Override
	public boolean match(int groupId) {
		return GROUP_ID == groupId;
	}

	@Override
	protected int getMinimumCards() {
		return MINIMUM_CARDS;
	}

	@Override
	protected List<Effect> groupAttack(Combat combat, PlayerId playing,
			List<BattleCard> groupCards) {
		List<Effect> effects = new ArrayList<>();
		PlayerId oppoPlayerId = Combat.getOppoId(playing);// 对手Id
		Player oppoPlayer = combat.getPlayer(oppoPlayerId);
		// 对手坟场卡牌
		List<BattleCard> discardCards = oppoPlayer.getDiscard();
		if (discardCards.isEmpty()) {
			return effects;
		}

		// 殷洪四天君：三人以上在场，每回合移除敌方一张墓地牌。四名以上在场，每回合移除敌方两张墓地卡。
		int remove = groupCards.size() > 3 ? 2 : 1;
		int minRemove = Math.min(remove, discardCards.size());
		// 坟场位置座标
		for (int i = 0; i < minRemove; i++) {
			BattleCard card = discardCards.get(i);
			CardPositionEffect effect = CardPositionEffect
					.getSkillEffectToTargetPos(GROUP_ID, card.getPos());
			effect.moveTo(PositionType.DEGENERATOR);
			effect.setAttackPower(AttackPower.getMaxPower());
			effect.setSequence(combat.getAnimationSeq());
			effects.add(effect);
		}
		return effects;
	}
}
