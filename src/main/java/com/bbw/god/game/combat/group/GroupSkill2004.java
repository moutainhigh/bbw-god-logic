package com.bbw.god.game.combat.group;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <font color=red>九曲黄河阵</font>
 * <font color=red>三宵同时在场，全体上阵卡牌攻防翻倍</font>。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2004 extends GroupSkillService {
	private static final int GROUP_ID = 2004;//组合ID
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
		//九曲黄河阵,三宵同时在场，全体上阵卡牌攻防翻倍。
		Player me = combat.getPlayer(playing);
		int seq=combat.getAnimationSeq();
		List<Effect> atks = new ArrayList<>();
		for (BattleCard card:me.getPlayingCards()){
			if (card!=null && card.isAlive()){
				CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
				effect.setHp(card.getHp());
				effect.setAtk(card.getAtk());
				effect.setSequence(combat.getAnimationSeq());
				atks.add(effect);
			}
		}
		return atks;
	}

}
