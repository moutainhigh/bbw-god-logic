package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 溅射 敌方卡牌对我方场上1张卡牌使用回合技能时将对目标相邻位置卡牌造成[2.5]%伤害。
 *
 * @author: suhq
 * @date: 2022/9/23 2:25 下午
 */
@Service
public class Runes331401 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.JIAN_SHE_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		Player performer = param.getPerformPlayer();
		Player opponenet = param.getOppoPlayer();
		CombatBuff combatBuff = performer.gainBuff(getRunesId());
		double harmRate = 0.025 * combatBuff.getLevel();
		int animationSeq = param.getNextSeq();
		List<Effect> receiveEffects = param.getReceiveEffect();
		for (Effect receiveEffect : receiveEffects) {
			List<Effect> effects = attackParticalEffects(performer, opponenet, receiveEffect, harmRate, animationSeq);
			if (ListUtil.isNotEmpty(effects)) {
				action.addEffects(effects);
			}
		}
		return action;
	}

	public List<Effect> attackParticalEffects(Player performer, Player oppPlayer, Effect receiveEffect, double harmRate, int animationSeq) {
		List<Effect> effects = new ArrayList<>();
		int performSkillId = receiveEffect.getPerformSkillID();
		//非回合技能不处理
		if (!isRoundSkill(performSkillId)) {
			return effects;
		}
		//己方发送的不处理
		if (receiveEffect.isEffectSelf()) {
			return effects;
		}
		//非伤害技能不处理
		if (!receiveEffect.isValueEffect()) {
			return effects;
		}
		CardValueEffect valueEffect = receiveEffect.toValueEffect();
		int targetIndex = PositionService.getBattleCardIndex(valueEffect.getTargetPos());
		boolean isYunTai = targetIndex == 0;
		//云台没有相邻位
		if (isYunTai) {
			return effects;
		}
		//获取相邻的卡牌
		List<BattleCard> linkCards = new ArrayList<>();
		BattleCard[] cards = performer.getPlayingCards();
		boolean isXianFeng = targetIndex == 1;
		if (isXianFeng) {
			linkCards.add(cards[targetIndex + 1]);
		} else {
			int leftIndex = targetIndex - 1;
			int rightIndex = targetIndex + 1;
			linkCards.add(cards[leftIndex]);
			if (rightIndex < cards.length - 1) {
				linkCards.add(cards[rightIndex]);
			}
		}
		linkCards = linkCards.stream().filter(tmp -> null != tmp).collect(Collectors.toList());

		boolean isForeverHarm = valueEffect.getRoundHp() > 0;
		int hp = valueEffect.getRoundHp();
		if (hp == 0) {
			hp = valueEffect.getHp();
		}
		hp = (int) (harmRate * hp);
		for (BattleCard card : linkCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
			if (isForeverHarm) {
				effect.setRoundHp(hp);
			} else {
				effect.setHp(hp);
			}
			effect.setSequence(animationSeq);
			effects.add(effect);
		}
		return effects;
	}

	/**
	 * 是否是回合技能
	 *
	 * @param performSkillId
	 * @return
	 */
	private boolean isRoundSkill(int performSkillId) {
		return performSkillId >= 3100 && performSkillId <= 3199;
	}
}
