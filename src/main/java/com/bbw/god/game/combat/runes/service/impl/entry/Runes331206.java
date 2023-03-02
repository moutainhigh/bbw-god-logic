package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 联合 每回合开始时，敌方场上每存在1张属性相同的卡牌，则这些卡牌增加[3]%的攻防值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:53 下午
 */
@Service
public class Runes331206 implements IRoundStageRunes {

	@Override
	public int getRunesId() {
		return RunesEnum.LIAN_HE_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		List<BattleCard> oppPlayingCards = param.getOppoPlayer().getPlayingCards(true);
		Map<TypeEnum, List<BattleCard>> cardGroupByType = oppPlayingCards.stream().collect(Collectors.groupingBy(BattleCard::getType));
		for (List<BattleCard> typeCards : cardGroupByType.values()) {
			// 计算攻防加成比例
			double addRate = 0.03 * typeCards.size() * combatBuff.getLevel();
			for (BattleCard card : typeCards) {
				int addHp = (int) (card.getRoundHp() * addRate);
				int addAck = (int) (card.getRoundAtk() * addRate);
				CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
				effect.setAtk(addAck);
				effect.setHp(addHp);
				action.addEffect(effect);
			}
		}
		return action;
	}
}
