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
 * （2018）组合技：行瘟使者
 * 三人同时在场，对方全体卡牌流失6%永久防御。
 * 四人以上同时在场流失12%永久防御。
 * 两人以上同时在场（必需有一个是吕岳），每一个行瘟使者在场可以使场上的吕岳的攻防提高15%。
 * 注：场上存在吕岳时  2人也可以出触发技能，但是仅有 使场上的吕岳的攻防提高15%的效果
 */
@Service
public class GroupSkill2018 extends GroupSkillService {
	private static final int GROUP_ID = 2018;//组合ID
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
		// 三人同时在场，对方全体卡牌流失6%永久防御。（四人以上同时在场流失12%永久防御。）每一个行瘟使者在场可以使场上的吕岳的攻防提高15%。
		List<Effect> effects=new ArrayList<>();
		int num=groupCards.size();
		if (num>2){
			int seq=combat.getAnimationSeq();
			BattleCard[] oppoPlayingCards = combat.getOppoPlayer(playing).getPlayingCards();
			double multiple=num==3?0.06:0.12;//三人同时在场，对方全体卡牌流失6%永久防御,四人以上同时在场流失12%永久防御。
			for (BattleCard card:oppoPlayingCards){
				if (card==null){
					continue;
				}
				CardValueEffect effect=CardValueEffect.getSkillEffect(GROUP_ID,card.getPos());
				effect.setHp(-getInt(card.getRoundHp()*multiple));
				effect.setSequence(seq);
				effects.add(effect);
			}
		}
		BattleCard[] myPlayingCards=combat.getPlayer(playing).getPlayingCards();
		BattleCard lvYue=null;
		for (BattleCard card:myPlayingCards){
			//是否含有吕岳（ID=535）
			if (card!=null && card.isAlive() && card.getImgId() == 535){
				lvYue=card;
				break;
			}
		}
		if (num>=2 && lvYue!=null){
			//每一个行瘟使者在场可以使场上的吕岳的攻防提高15%。
			int seq=combat.getAnimationSeq();
			CardValueEffect effect=CardValueEffect.getSkillEffect(GROUP_ID,lvYue.getPos());
			effect.setHp(getInt(lvYue.getHp()*((num-1)*0.3)));
			effect.setAtk(getInt(lvYue.getAtk()*((num-1)*0.3)));
			effect.setSequence(seq);
			effects.add(effect);
		}
		return effects;
	}
}
