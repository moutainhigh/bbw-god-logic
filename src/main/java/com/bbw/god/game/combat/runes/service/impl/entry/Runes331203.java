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
 * 闪雷 每回合开始时，敌方对我方场上1张卡牌施放1次雷系技能，减少[400]点防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:10 上午
 */
@Service
public class Runes331203 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.SHAN_LEI_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		Player performPlayer = param.getPerformPlayer();
		List<BattleCard> playingCards = performPlayer.getPlayingCards(true);
		if (ListUtil.isEmpty(playingCards)) {
			return action;
		}
		BattleCard targetCard = PowerRandom.getRandomFromList(playingCards);
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		int hp = 400 + 200 * (combatBuff.getLevel() -1);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
		effect.setPerformSkillID(getRunesId());
		effect.setHp(-hp);
		effect.setSequence(param.getNextSeq());
		action.addEffect(effect);
		return action;
	}
}
