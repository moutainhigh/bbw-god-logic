package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 灵敏 敌方卡牌受到普通攻击时，有[5]%概率回避本次攻击。
 *
 * @author: suhq
 * @date: 2022/9/22 4:27 下午
 */
@Service
public class Runes331302 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.LING_MIN_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action ar = new Action();
		List<Effect> receiveEffects = param.getReceiveEffect();
		if (ListUtil.isEmpty(receiveEffects)) {
			return ar;
		}
		for (Effect effect : receiveEffects) {
			if (isPerformSelf(effect.getTargetPos(), param.getPerformPlayer().getId())) {
				continue;
			}
			//仅对普通攻击有效
			if (CombatSkillEnum.NORMAL_ATTACK.getValue() != effect.getPerformSkillID()) {
				continue;
			}
			CardValueEffect cardValueEffect = effect.toValueEffect();

			CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
			//反弹比例
			int avoidRate = 5 * combatBuff.getLevel();
			if (!PowerRandom.hitProbability(avoidRate)) {
				continue;
			}
			//将效果置为无效
			cardValueEffect.setValid(false);
			//触发 补充一个动画
			AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), cardValueEffect.getSourcePos());
			ar.addClientAction(amin);
		}
		return ar;
	}
}
