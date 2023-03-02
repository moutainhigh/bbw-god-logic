package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
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
 * 反甲 敌方卡牌受到普通攻击后，反弹[5]%本次攻击受到的伤害。
 *
 * @author: suhq
 * @date: 2022/9/22 4:14 下午
 */
@Service
public class Runes331303 implements IRoundStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.FAN_JIA_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action ar = new Action();
		ar.setNeedAddAnimation(false);
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
			//召唤师不触发效果
			if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
				continue;
			}
			CardValueEffect cardValueEffect = effect.toValueEffect();
			int hp = cardValueEffect.getHp() + cardValueEffect.getRoundHp();
			if (hp >= 0) {
				continue;
			}
			CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
			//反弹比例
			double reboundRate = 0.05 * combatBuff.getLevel();
			int reboundHp = (int) (hp * reboundRate);
			//构建反弹效果
			CardValueEffect valueEffect = CardValueEffect.getSkillEffect(getRunesId(), cardValueEffect.getSourcePos());
			valueEffect.setHp(reboundHp);
			ar.addEffect(valueEffect);
			//触发 补充一个动画
//			AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), cardValueEffect.getSourcePos());
//			ar.addClientAction(amin);
		}
		return ar;
	}
}
