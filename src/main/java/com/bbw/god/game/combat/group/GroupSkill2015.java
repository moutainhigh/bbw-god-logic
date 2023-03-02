package com.bbw.god.game.combat.group;

import com.bbw.common.ListUtil;
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
 * <font color=red>神力横扫</font>
 * 神力横扫	两人同时在场，该回合对敌方全体卡牌施加一次攻击，伤害为两人攻击的较低者。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2015 extends GroupSkillService {
	private static final int GROUP_ID = 2015;//组合ID
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
		//神力横扫	两人同时在场，该回合对敌方全体卡牌施加一次攻击，伤害为两人攻击的较低者。
		PlayerId oppoPlayerId = Combat.getOppoId(playing);//对手Id
		List<BattleCard> oppoPlayingCards = combat.getPlayingCards(oppoPlayerId, false);//对手卡牌
		List<Effect> atks = new ArrayList<>(0);
		if (ListUtil.isEmpty(oppoPlayingCards)){
			return atks;
		}
		int atk = Math.min(groupCards.get(0).getAtk(), groupCards.get(1).getAtk());
		int seq = combat.getAnimationSeq();
		for (BattleCard card : oppoPlayingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.setHp(-atk);
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}
}
