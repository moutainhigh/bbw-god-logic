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
 * 梅山七怪	两人在场，该回合每人攻防各加100，每多一人攻防再额外加100,梅山七怪每位上场成员每一阶额外提高10%的攻防提升。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2010 extends GroupSkillService {
	private static final int GROUP_ID = 2010;//组合ID
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
		//梅山七怪 两人在场，该回合每人攻防各加100，每多一人攻防再额外加100,梅山七怪每位上场成员每一阶额外提高10%的攻防提升。
		int atk = (groupCards.size() - 1) * 100;
		int hp = (groupCards.size() - 1) * 100;
		List<Effect> atks = new ArrayList<>(groupCards.size());
		int seq = combat.getAnimationSeq();
		int sumHv=groupCards.stream().collect(Collectors.summingInt(BattleCard::getHv));
		int addAtk=getInt(atk*(1+sumHv*0.1));
		int addHp=getInt(hp*(1+sumHv*0.1));
		for (BattleCard card : groupCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.setAtk(addAtk);
			effect.setHp(addHp);
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}

}
