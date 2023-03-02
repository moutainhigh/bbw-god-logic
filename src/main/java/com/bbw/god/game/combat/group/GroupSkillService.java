package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bbw.god.game.combat.data.*;
import org.springframework.lang.NonNull;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;

/**
 * 组合基本服务
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 00:04
 */
public abstract class GroupSkillService {

	/**
	 * 获取组合的卡牌
	 * @param groupId:组合的ID
	 * @param playingCards:上阵的卡牌
	 * @return
	 */
	@NonNull
	private List<BattleCard> getGroupCards(int groupId, final BattleCard[] playingCards) {
		List<BattleCard> groupCards = new ArrayList<>(CombatConfig.MAX_BATTLE_CARD);
		for (BattleCard card : playingCards) {
			if (null != card && card.getGroupId() == groupId) {
				groupCards.add(card);
			}
		}
		return groupCards;
	}

	/**
	 * 组合攻击
	 * @param groupId
	 * @param combat
	 * @param playing
	 * @return
	 */
	@NonNull
	public Optional<Action> attack(int groupId, Combat combat, Player player) {
		List<BattleCard> groupCards = this.getGroupCards(groupId, player.getPlayingCards());
		//没有形成组合
		if (groupCards.size() < getMinimumCards()) {
			return Optional.empty();
		}
		Action ar = new Action();
		List<Effect> effects = groupAttack(combat, player.getId(), groupCards);
		if (effects.isEmpty()) {
			return Optional.of(ar);
		}
		for (Effect effect : effects) {
			if (effect.getSourcePos() == -1) {
				int pos = PowerRandom.getRandomFromList(groupCards).getPos();
				effect.setSourcePos(pos);
			}
		}
		//形成了组合
		AnimationSequence as = getGroupCardsActions(groupId, groupCards, combat.getAnimationSeq());
		ar.addClientAction(as);
		//添加客户端动画
		combat.addAnimation(as);
		ar.setEffects(effects);
		return Optional.of(ar);
	}

	/**
	 * 将攻击翻译成客户端动画播放序列
	 * @param groupId: 组合技能ID
	 * @param groupCards: 组合的卡牌
	 * @return
	 */
	private AnimationSequence getGroupCardsActions(int groupId, List<BattleCard> groupCards, int sequence) {
		AnimationSequence as = new AnimationSequence(sequence, EffectResultType.PLAY_ANIMATION);
		//客户端动画
		for (BattleCard card : groupCards) {
			Animation action = new Animation();
			action.setSkill(groupId);
			action.setPos1(card.getPos());
			as.add(action);
		}
		return as;
	}

	/**
	 * 是否是组合id对应的服务
	 * @param groupId
	 * @return
	 */
	public abstract boolean match(int groupId);

	/**
	 * 最少需要多少张组合卡才能形成组合
	 * @return
	 */
	protected abstract int getMinimumCards();

	/**
	 * 组合攻击
	 * @param combat:战斗数据对象
	 * @param playingId:当前出手的玩家ID
	 * @param groupCards:当前组合的卡牌集合
	 * @return
	 */
	protected abstract List<Effect> groupAttack(Combat combat, PlayerId playingId, List<BattleCard> groupCards);

	protected int getInt(Double d){
		return d.intValue();
	}

}
