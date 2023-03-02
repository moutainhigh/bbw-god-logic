package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 瘟君 上场时敌方全体卡牌（不含云台）进入中毒状态，无视回光，每轮损失80点永久防御。每升1阶增加50%效果。（无视金刚）
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 14:07
 */
@Service
public class BattleSkill1007 extends BattleSkillService {
	private static final int SKILL_ID = 1007;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 瘟君 上场时敌方全体卡牌（不含云台）进入中毒状态，无视回光，每轮损失80点永久防御。每升1阶增加50%效果。（无视金刚）
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);// 对手卡牌
		double hp = 80 * (1 + 0.5 * psp.getPerformCard().getHv());
		int sequence = psp.getNextAnimationSeq();
		for (BattleCard card : oppoPlayingCards) {
			// 本轮伤害
			CardValueEffect effect = CardValueEffect.getSkillLastingEffect(
					SKILL_ID, card.getPos(), psp.getCombat().getRound());
			effect.setValueType(CardValueEffectType.LASTING);
			effect.setRoundHp(-this.getInt(hp));
			effect.setSequence(sequence);
			effect.setTimesLimit(TimesLimit.noLimit());
			ar.addEffect(effect);
		}
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp, int attkPos) {
		// 瘟君 反弹伤害 只反弹一轮伤害
		List<Effect> res = new ArrayList<>();
		List<Effect> effects = super.attackTargetPosByCopyEffects(fromPsp, attkPos);
		for (Effect effect : effects) {
			CardValueEffect ef = effect.toValueEffect();
			ef.setValueType(CardValueEffectType.IN_TIME);
			ef.setTimesLimit(TimesLimit.oneTimeLimit());
			res.add(ef);
		}
		return res;
	}
}
