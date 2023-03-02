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
import java.util.Optional;

/**
 * 入痘 每回合使敌方一张卡牌进入中毒状态（不含云台），无视回光，每轮损失80点永久防御，该状态可累加。每升一阶增加50%效果。（无视金刚）
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:56
 */
@Service
public class BattleSkill3127 extends BattleSkillService {
	private static final int SKILL_ID = 3127;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 入痘 每回合使敌方一张卡牌进入中毒状态（不含云台），无视回光，每轮损失80点永久防御，该状态可累加。每升一阶增加50%效果。（无视金刚）
		Optional<BattleCard> targetCard = psp.randomOppoPlayingCard(false);
		if (targetCard.isPresent()){
			return buildEffects(targetCard.get(),psp);
		}
		return new Action();
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,
			int attkPos) {
		// 入痘 反弹伤害 只反弹一轮伤害
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

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		// 入痘 每回合使敌方一张卡牌进入中毒状态（不含云台），无视回光，每轮损失80点永久防御，该状态可累加。每升一阶增加50%效果。（无视金刚）
		Action attackResult = new Action();
		double hp = 80 * (1 + 0.5 * psp.getPerformCard().getHv());
		// 添加永久伤害
		CardValueEffect atk = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
		atk.setValueType(CardValueEffectType.LASTING);
		atk.setRoundHp(-this.getInt(hp));
		atk.setSequence(psp.getNextAnimationSeq());
		atk.setBeginRound(psp.getCombat().getRound());
		attackResult.addEffect(atk);
		return attackResult;
	}
}
