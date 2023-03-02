package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奋战 每回合结束时，提高敌方全体卡牌[1]%永久攻击值，[1]%永久防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:10 上午
 */
@Service
public class Runes331408 implements IRoundEndStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.FEN_ZHAN_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundEndRunes(CombatRunesParam param) {
		Action action = new Action();
		Player oppPlayer = param.getOppoPlayer();
		List<BattleCard> oppPlayingCards = oppPlayer.getPlayingCards(true);
		if (ListUtil.isEmpty(oppPlayingCards)) {
			return action;
		}
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		double attackRate = 0.01 * combatBuff.getLevel();
		double hpRate = 0.01 * combatBuff.getLevel();
		int animationSeq = param.getNextSeq();
		for (BattleCard targetCard : oppPlayingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
			effect.setRoundHp((int) (targetCard.getHp() * hpRate));
			effect.setRoundAtk((int) (targetCard.getAtk() * attackRate));
			effect.setSequence(animationSeq);
			action.addEffect(effect);
		}
		action.setTakeEffect(true);
		return action;
	}
}
