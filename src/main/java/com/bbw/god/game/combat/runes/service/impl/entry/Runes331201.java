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
 * 蛇毒 每回合开始时，敌方对我方场上2张卡牌施放1次毒系技能，进入中毒状态：每回合破除[130]点永久防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:10 上午
 */
@Service
public class Runes331201 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.SHE_DU_ENTRY.getRunesId();
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
		if (playingCards.size() > 2) {
			targetCards = PowerRandom.getRandomsFromList(playingCards, 2);
		}
		CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
		int roundHp = 130 + 30 * (combatBuff.getLevel() - 1);
		int sequence = param.getNextSeq();
		for (BattleCard targetCard : targetCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
			effect.setPerformSkillID(getRunesId());
			effect.setValueType(CardValueEffect.CardValueEffectType.LASTING);
			effect.setBeginRound(param.getRound());
			effect.setRoundHp(-roundHp);
			effect.setSequence(sequence);
			action.addEffect(effect);
		}
		return action;
	}
}
