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
 * <font color=red>四大天王</font>
 * 三人在场，对方全体卡牌（不含云台）攻防<font color=red>永久各减150</font>，四人同时在场，攻防<font color=red>永久各减300</font>。
 * </pre>增加升阶效果，根据场上最低阶数的四大天王，每阶提高30%的永久攻防降低。
 * @author lsj@bamboowind.cn
 * @version 1.0.0 
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2006 extends GroupSkillService {
	private static final int GROUP_ID = 2006;//组合ID
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
		//四大天王: 三人在场，每回合使敌方全体卡牌（不含云台）攻防永久减150，四人同时在场，攻防永久减300。增加升阶效果，根据场上最低阶数的四大天王，每阶提高30%的永久攻防降低。
		if (groupCards.size()<3){
			return new ArrayList<>();
		}
		PlayerId oppoPlayerId = Combat.getOppoId(playing);//对手Id
		List<BattleCard> playingCards = combat.getPlayingCards(oppoPlayerId, false);//对手卡牌
		int atkValue = getAttackValue(groupCards);
		List<Effect> atks = new ArrayList<>(playingCards.size());
		int seq = combat.getAnimationSeq();
		for (BattleCard card : playingCards) {
			CardValueEffect atk = CardValueEffect.getSkillEffect(GROUP_ID, card.getPos());
			atk.setRoundAtk(-atkValue);
			atk.setRoundHp(-atkValue);
			atk.setSequence(seq);
			atks.add(atk);
		}
		return atks;
	}

	/**
	 * 增加升阶效果，根据场上最低阶数的四大天王，每阶提高30%的永久攻防降低。
	 * @param groupCards
	 * @return
	 */
	private int getAttackValue(List<BattleCard> groupCards) {
		int groupCardCount=groupCards.size();
		int val=0;
		if (groupCardCount==3){
			val=150;
		}else if (groupCardCount==4){
			val=300;
		}
		int hv=10;
		for (BattleCard card:groupCards){
			if (card.getHv()<hv){
				hv=card.getHv();
			}
		}
		Double dval=Double.valueOf(val*(1+hv*0.3));
		return dval.intValue();
	}

}
