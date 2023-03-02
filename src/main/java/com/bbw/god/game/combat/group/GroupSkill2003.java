package com.bbw.god.game.combat.group;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * <font color=red>陈塘家</font>
 * <font color=red>两人</font>以上在场，阵中<font color=red>全体卡牌 攻防 各加100</font>。
 * </pre>陈塘家每位上场成员每一阶额外提高10%的攻防提升。
 * @author lsj@bamboowind.cn 
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2003 extends GroupSkillService {
	private static final int GROUP_ID = 2003;//组合ID
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
		//陈塘家,两人以上在场，阵中全体卡牌攻防各加100 陈塘家每位上场成员每一阶额外提高10%的攻防提升。
		List<BattleCard> playingCards = combat.getPlayingCards(playing, true);
		int atk = 100;
		int hp = 100;
		List<Effect> atks = new ArrayList<>(playingCards.size());
		int seq = combat.getAnimationSeq();
		int sumHv=groupCards.stream().collect(Collectors.summingInt(BattleCard::getHv));
		int addAtk=getInt(atk*(1+sumHv*0.1));
		int addHp=getInt(hp*(1+sumHv*0.1));
		for (BattleCard card : playingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.setAtk(addAtk);
			effect.setHp(addHp);
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}

}
