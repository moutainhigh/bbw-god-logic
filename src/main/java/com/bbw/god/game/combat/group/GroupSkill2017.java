package com.bbw.god.game.combat.group;

import com.bbw.common.PowerRandom;
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
 * <font color=red>古兽来袭</font>
 * 三张以上在场，随机杀死一张受到过永远伤害的卡牌。
 */
@Service
public class GroupSkill2017 extends GroupSkillService {
	private static final int GROUP_ID = 2017;//组合ID
	private static final int MINIMUM_CARDS = 3;//至少需要多少张才能形成组合

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
		// 三张以上在场，随机杀死一张受到过永远伤害的卡牌。
		List<Effect> atks=new ArrayList<>();
		BattleCard[] playingCards = combat.getOppoPlayer(playing).getPlayingCards();
		List<BattleCard> cards=new ArrayList<>();
		for (BattleCard card:playingCards){
			if (card!=null && card.getInitHp()>card.getRoundHp()){
				cards.add(card);
			}
		}
		if (!cards.isEmpty()){
			BattleCard target=PowerRandom.getRandomFromList(cards);
			CardValueEffect effect=CardValueEffect.getSkillEffect(GROUP_ID,target.getPos());
			effect.setHp(-target.getHp());
			atks.add(effect);
		}
		return atks;
	}
}
