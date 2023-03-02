package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;

/**
 * <pre>
 * <font color=red>十绝阵</font>
 * <font color=red>两人</font>以上在场，当前回合回合给对方<font color=red>召唤师</font>造成在场十绝阵成员<font color=red>总星级*150</font>的伤害。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 00:17
 */
@Service
public class GroupSkill2001 extends GroupSkillService {
	//private static final String GROUP_NAME = "十绝阵";
	private static final int GROUP_ID = 2001;//组合ID
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
		//十绝阵,两人以上在场，当前回合给对方召唤师造成在场十绝阵成员总星级*150的伤害。
		//总星数
		int totalStars = groupCards.stream().collect(Collectors.summingInt(BattleCard::getStars));
		List<Effect> effects = new ArrayList<>(1);
		int targetPos = PositionService.getZhaoHuanShiPos(Combat.getOppoId(playing));
		CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, targetPos);
		effect.setHp(-totalStars * 150);
		effect.setSequence(combat.getAnimationSeq());
		effects.add(effect);
		return effects;
	}

}
