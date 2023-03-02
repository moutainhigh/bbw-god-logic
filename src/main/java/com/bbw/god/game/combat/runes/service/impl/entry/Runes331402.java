package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 硬甲 敌方卡牌不会受到小于自身[3]%最大防御值的伤害。
 *
 * @author: suhq
 * @date: 2022/9/22 4:27 下午
 */
@Service
public class Runes331402 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.YING_JIA_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		List<Effect> receiveEffects = param.getReceiveEffect();
		if (ListUtil.isEmpty(receiveEffects)) {
			return action;
		}
		CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
		for (Effect receiveEffect : receiveEffects) {
			//非伤害效果不处理
			if (!receiveEffect.isValueEffect()) {
				continue;
			}
			//作用于自身的效果不处理
			if (receiveEffect.isEffectSelf()) {
				continue;
			}
			CardValueEffect valueEffect = receiveEffect.toValueEffect();
			if (valueEffect.getRoundHp() > 0 || valueEffect.getHp() > 0) {
				continue;
			}
			int targetIndex = PositionService.getBattleCardIndex(valueEffect.getTargetPos());
			BattleCard targetCard = param.getOppoPlayer().getPlayingCards(targetIndex);
			if (null == targetCard) {
				continue;
			}
			int ableDefenceValue = -(int) (0.03 * combatBuff.getLevel() * targetCard.getInitHp());
			boolean cantAvoid = valueEffect.getRoundMp() < ableDefenceValue || valueEffect.getHp() < ableDefenceValue;
			if (cantAvoid) {
				continue;
			}
			valueEffect.setValid(false);
			valueEffect.setDefended(true);
			//触发 补充一个动画
			AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), valueEffect.getSourcePos());
			action.addClientAction(amin);
		}
		return action;
	}
}
