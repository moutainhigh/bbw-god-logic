package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 压制 每回合开始时，敌方场上每存在1张不同属性的卡牌，则减少我方全体卡牌[1]%的攻防值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:53 下午
 */
@Service
public class Runes331205 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.YA_ZHI_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		List<BattleCard> oppPlayingCards = param.getOppoPlayer().getPlayingCards(true);
		long differentTypeCount = oppPlayingCards.stream().map(BattleCard::getType).distinct().count();
		// 计算攻防加成比例
		double addRate = 0.01 * differentTypeCount * combatBuff.getLevel();
		for (BattleCard card : param.getPerformPlayer().getPlayingCards(true)) {
			int addHp = (int) (card.getRoundHp() * addRate);
			int addAck = (int) (card.getRoundAtk() * addRate);
			CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
			effect.setAtk(-addAck);
			effect.setHp(-addHp);
			action.addEffect(effect);
		}
		return action;
	}
}
