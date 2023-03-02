package com.bbw.god.game.combat.group;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 【上古神力】 2020：两人在场，将敌方所有地面卡牌送入坟场，一次战斗触发一次。两人在场，该回合对敌方地面卡牌造成一次在场成员中较低攻击值的伤害。
 *
 * @author longwh
 * @date 2022/11/17 11:50
 */
@Service
public class GroupSkill2020 extends GroupSkillService {
	private static final int GROUP_ID = CombatSkillEnum.SGSL_G.getValue();
	private static final int MINIMUM_CARDS = 2;

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
		List<Effect> atks = new ArrayList<>(0);
		//对手
		PlayerId oppoPlayerId = Combat.getOppoId(playing);
		//对手卡牌
		List<BattleCard> oppoPlayingCards = combat.getPlayingCards(oppoPlayerId, false);
		// 一次战斗触发一次
		int qiLinEffectTimes = combat.getPlayer(playing).getStatistics().gainSkillEffectTime(CombatSkillEnum.SGSL_G);
		if (qiLinEffectTimes < 1) {
			// 将敌方所有地面卡牌送入坟场
			for (BattleCard oppoCard : oppoPlayingCards) {
				CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(GROUP_ID, oppoCard.getPos());
				effect.moveTo(PositionType.DISCARD);
				effect.setSequence(combat.getAnimationSeq());
				atks.add(effect);
			}
			combat.getPlayer(playing).getStatistics().addSkillEffectTime(CombatSkillEnum.SGSL_G);
		}else {
			//两人在场，该回合对敌方地面卡牌造成一次在场成员中较低攻击值的伤害。
			if (ListUtil.isEmpty(oppoPlayingCards)){
				return atks;
			}
			int atk = Math.min(groupCards.get(0).getAtk(), groupCards.get(1).getAtk());
			int seq = combat.getAnimationSeq();
			for (BattleCard card : oppoPlayingCards) {
				CardValueEffect effect = CardValueEffect.getSkillEffect(CombatSkillEnum.SGSL_G.getValue(), card.getPos());
				effect.setHp(-atk);
				effect.setSequence(seq);
				atks.add(effect);
			}
		}
		return atks;
	}
}