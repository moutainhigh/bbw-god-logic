package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;

/**
 * <pre>
 * <font color=red>晁式兄弟</font>
 * <font color=red>两人</font>同时在场,两人同时在场，直接杀死敌方场上防御最低的卡牌(不含云台），无视金刚。
 * </pre> 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2013 extends GroupSkillService {
	private static final int GROUP_ID = 2013;//组合ID
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
		//晁式兄弟: 两人同时在场，两人同时在场，直接杀死敌方场上防御最低的卡牌(不含云台），无视金刚。
		PlayerId oppoPlayerId = Combat.getOppoId(playing);//对手Id
		List<BattleCard> playingCards = combat.getPlayingCards(oppoPlayerId, false);//对手卡牌
		if (playingCards.isEmpty()) {
			return new ArrayList<>();
		}
		BattleCard attackCard = getAttackCard(playingCards);
		List<Effect> atks = new ArrayList<>(1);
		CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, attackCard.getPos());
		//该技能造成的伤害为永久性伤害
		effect.setRoundHp(-attackCard.getRoundHp());
		effect.setAttackPower(AttackPower.L2);
		effect.setSequence(combat.getAnimationSeq());
		atks.add(effect);
		return atks;
	}

	private BattleCard getAttackCard(List<BattleCard> playingCards) {
		int minHp = 0;
		int index = 0;
		for (int i = 0; i < playingCards.size(); i++) {
			if (PositionService.isYunTaiPos(playingCards.get(i).getPos())) {
				continue;
			}
			//防御最低
			if (minHp > playingCards.get(i).getHp()) {
				minHp = playingCards.get(i).getHp();
				index = i;
			}else if (minHp==0) {
				minHp = playingCards.get(i).getHp();
				index = i;
			}
		}
		return playingCards.get(index);
	}

}
