package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;

/**
 * <pre>
 * 左右门神	两人同时在场，该回合我方全体卡牌防御值各加150,左右门神每位上场成员每一阶额外提高20%的防御提升。。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2011 extends GroupSkillService {
	private static final int GROUP_ID = 2011;//组合ID
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
		//左右门神两人同时在场，该回合我方全体卡牌防御值各加150,左右门神每位上场成员每一阶额外提高20%的防御提升。。
		int hv=groupCards.stream().collect(Collectors.summingInt(BattleCard::getHv));
		int hpValue = getInt(150*(1+0.2*hv));
		List<BattleCard> playingCards = combat.getPlayingCards(playing, true);
		List<Effect> atks = new ArrayList<>(playingCards.size());
		int seq = combat.getAnimationSeq();
		for (BattleCard card : playingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.setHp(hpValue);
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}

}
