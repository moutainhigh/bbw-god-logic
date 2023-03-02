package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 箭阵 每回合开始时，敌方对我方场上3张卡牌施放1次狙系技能，破除[150]点永久防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:10 上午
 */
@Service
public class Runes331204 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.JIAN_ZHEN_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		Player performPlayer = param.getPerformPlayer();
		List<BattleCard> playingCards = performPlayer.getPlayingCards(true);
		if (ListUtil.isEmpty(playingCards)) {
			return action;
		}
		List<BattleCard> targetCards = playingCards;
		if (playingCards.size() > 3) {
			targetCards = PowerRandom.getRandomsFromList(playingCards, 3);
		}
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		int roundHp = 150 + 50 * (combatBuff.getLevel() -1);
		int sequence = param.getNextSeq();
		for (BattleCard targetCard : targetCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
			effect.setPerformSkillID(getRunesId());
			effect.setRoundHp(-roundHp);
			effect.setSequence(sequence);
			action.addEffect(effect);
		}
		return action;
	}
}
