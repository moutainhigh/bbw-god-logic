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
 * <font color=red>黄家军</font>
 * <font color=red>两人</font>以上在场，<font color=red>攻击各加300</font>。
 * 两人以上在场，攻击各加300,黄家军每位上场成员每一阶额外提高10%的攻击提升。（最高每人可加1500）
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2002 extends GroupSkillService {
	private static final int GROUP_ID = 2002;//组合ID
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
		//黄家军:两人以上在场，攻击各加300,黄家军每位上场成员每一阶额外提高10%的攻击提升。（阶数为黄家军总阶数）
		int atk = 300;
		List<Effect> effects = new ArrayList<>(groupCards.size());
		int seq = combat.getAnimationSeq();
		int sumHv=groupCards.stream().collect(Collectors.summingInt(BattleCard::getHv));
		int addAtk=getInt(atk*(1+sumHv*0.1));
		for (BattleCard card : groupCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.setAtk(addAtk);
			effect.setSequence(seq);
			effects.add(effect);
		}


		return effects;
	}

}