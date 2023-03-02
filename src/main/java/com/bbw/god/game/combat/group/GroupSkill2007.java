package com.bbw.god.game.combat.group;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <font color=red>哼哈二将</font>
 * <font color=red>两人</font>同时在场,<font color=red>每回合</font>使对方场上攻防最高的卡牌（不含云台）攻防值<font color=red>永久减半</font>。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2007 extends GroupSkillService {
	private static final int GROUP_ID = 2007;//组合ID
	private static final int MINIMUM_CARDS = 2;//至少需要多少张才能形成组合

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
		//哼哈二将: 两人同时在场，每回合使敌方场上攻防之和最高的卡牌（不含云台）攻防值永久减半。
		PlayerId oppoPlayerId = Combat.getOppoId(playing);//对手Id
		List<BattleCard> playingCards = combat.getPlayingCards(oppoPlayerId, false);//对手战场牌
		if (playingCards.isEmpty()) {
			return  new ArrayList<>();
		}
		BattleCard attackCard = getAttackCard(playingCards);
		List<Effect> atks = new ArrayList<>(1);
		CardValueEffect atk = CardValueEffect.getSkillEffect(GROUP_ID, attackCard.getPos());
		atk.setRoundAtk(-attackCard.getRoundAtk() / 2);
		atk.setRoundHp(-attackCard.getRoundHp() / 2);
		atk.setSequence(combat.getAnimationSeq());
		atks.add(atk);
		return atks;
	}

	private BattleCard getAttackCard(List<BattleCard> playingCards) {
		int maxValue = 0;
		int index = 0;
		for (int i = 0; i < playingCards.size(); i++) {
			int value = playingCards.get(i).getRoundAtk() + playingCards.get(i).getRoundHp();
			if (value > maxValue) {
				maxValue = value;
				index = i;
			}
		}
		return playingCards.get(index);
	}

}
