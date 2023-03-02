package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;

/**
 * <pre>
 * 千里眼顺风耳	两人同时在场，每回合使敌方法术值永久损失1。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2008 extends GroupSkillService {
	private static final int GROUP_ID = 2008;//组合ID
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
		//千里眼顺风耳:	两人同时在场，每回合使敌方法术值永久损失1。
		//对手召唤师
		int targetPos = combat.getOppoZhaoHuanShiPos(playing);
		List<Effect> atks = new ArrayList<>(1);
		CardValueEffect effect = CardValueEffect.getSkillEffect(GROUP_ID, targetPos);
		effect.setRoundMp(-1);
		effect.setSequence(combat.getAnimationSeq());
		atks.add(effect);
		return atks;
	}
}
