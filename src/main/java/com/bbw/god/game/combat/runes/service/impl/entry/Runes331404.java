package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 撤离	敌方卡牌进入坟场后，有[6]%概率返回牌堆。
 *
 * @author: suhq
 * @date: 2022/9/23 3:30 下午
 */
@Service
public class Runes331404 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.CHE_LI_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		//己方不处理
		if (!param.isEnemyTargetCard()) {
			return action;
		}
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		int rate = 6 * combatBuff.getLevel();
		if (!PowerRandom.hitProbability(rate)) {
			return action;
		}
		CardPositionEffect positionEffect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), param.getTargetCard().getPos());
		positionEffect.setSourceID(getRunesId());
		positionEffect.setToPositionType(PositionType.DRAWCARD);
		action.addEffect(positionEffect);
		return action;
	}

}
