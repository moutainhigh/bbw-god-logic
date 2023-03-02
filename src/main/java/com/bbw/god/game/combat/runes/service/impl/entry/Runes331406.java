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
 * 轮转 每回合开始时，若当前为奇数回合则敌方全体卡牌增加[10]%攻击值，若为偶数回合则增加[10]%防御值。
 *
 * @author: suhq
 * @date: 2022/9/23 9:10 上午
 */
@Service
public class Runes331406 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.LUN_ZHUAN_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		Player oppPlayer = param.getOppoPlayer();
		List<BattleCard> oppPlayingCards = oppPlayer.getPlayingCards(true);
		if (ListUtil.isEmpty(oppPlayingCards)) {
			return action;
		}
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		double rate = 0.1 * combatBuff.getLevel();
		int round = param.getRound();
		int animationSeq = param.getNextSeq();
		for (BattleCard targetCard : oppPlayingCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
			if (round % 2 == 0) {
				effect.setHp((int) (targetCard.getHp() * rate));
			} else {
				effect.setAtk((int) (targetCard.getAtk() * rate));
			}
			effect.setSequence(animationSeq);
			action.addEffect(effect);
		}
		return action;
	}
}
