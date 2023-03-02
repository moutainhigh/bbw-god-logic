package com.bbw.god.game.combat.group;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <font color=red>迷魂阵</font>
 * <font color=red>三人</font>同时在场，<font color=red>该回合使敌方全体卡牌（不含云台）技能失效</font>。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0 
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2005 extends GroupSkillService {
	private static final int GROUP_ID = 2005;//组合ID
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
		//迷魂阵,三人同时在场，该回合使敌方全体卡牌（不含云台）技能失效。
		PlayerId oppoPlayerId = Combat.getOppoId(playing);//对手Id
		//获取对手卡牌（不包含云台）
		List<BattleCard> oppPlayingCards = combat.getPlayingCards(oppoPlayerId, false);
		List<Effect> atks = new ArrayList<>(0);
		int seq = combat.getAnimationSeq();
//		int skillId = 3131;//封咒效果
		for (BattleCard card : oppPlayingCards) {
			List<BattleSkill> skills = card.getSkills();
			if (skills.isEmpty()) {
				continue;
			}
			BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(GROUP_ID, card.getPos());
			for (BattleSkill skill : skills) {
				TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
				effect.forbidOneRound(skill.getId(), limt, GROUP_ID);
				effect.setSequence(seq);
			}
			atks.add(effect);
		}
		return atks;
	}

}
