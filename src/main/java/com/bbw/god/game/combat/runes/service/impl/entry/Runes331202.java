package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
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
 * 烈火 每回合开始时，敌方对我方全体卡牌施放1次火系技能，减少[300]点防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:27 上午
 */
@Service
public class Runes331202 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.LIE_HUO_ENTRY.getRunesId();
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
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		//伤害值
		int hp = 300 + 100 * (combatBuff.getLevel() - 1);

		int sequence = param.getNextSeq();
		for (BattleCard targetCard : targetCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
			effect.setPerformSkillID(getRunesId());
			effect.setHp(-hp);
			effect.setSequence(sequence);
			action.addEffect(effect);
		}
		return action;
	}
}
