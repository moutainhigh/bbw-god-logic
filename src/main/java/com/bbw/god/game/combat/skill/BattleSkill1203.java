package com.bbw.god.game.combat.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus.StatusEffectType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 死咒 被击退后，致死的卡牌的攻防永久减半，每升一阶攻防再降3%。（无视金刚）
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill1203 extends BattleDieSkill {
	private static final int SKILL_ID = 1203;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
	@Override
	protected Action performSkill(PerformSkillParam psp) {
		Action action = new Action();
		// 死咒 被击退后，致死的卡牌的攻防永久减半，每升一阶攻防再降3%。（无视金刚）
		int targetPos = 0;
		// 组合技致死则 随机反弹到一张卡牌
		if (psp.getReceiveEffect().isFromGroupSkill()) {
			int groupSkillId = psp.getReceiveEffectSkillId();
			List<BattleCard> cards = psp.getOppoPlayer().getPlayingCardsByGroupSkillId(groupSkillId);
			BattleCard target = PowerRandom.getRandomFromList(cards);
			targetPos = target.getPos();
		} else {
			// 单体致死
			Optional<BattleCard> sourceCard = psp.getEffectSourceCard();
			if (!sourceCard.isPresent() || sourceCard.get().isKilled()) {
				// 伤害来源卡 已死或不存在 则不发动技能
				return action;
			}
			targetPos = sourceCard.get().getPos();
		}
		if (targetPos == 0) {
			return action;
		}

		BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(StatusEffectType.ROUND_END, SKILL_ID, targetPos);
		effect.setSouceCard(CloneUtil.clone(psp.getPerformCard()));
		action.addEffect(effect);
		return action;
	}

	@Override
	public List<Effect> attakRoundLasting(PerformSkillParam psp) {
		List<Effect> effects = new ArrayList<Effect>();
		BattleCard performCard = psp.getPerformCard();
		BattleCard targetCard = psp.getFaceToFaceCard().get();
		Double atkValue = targetCard.getRoundAtk() * (0.5 + 0.03 * performCard.getHv());
		int roundAtk = this.getInt(atkValue);
		Double hpValue = targetCard.getRoundHp() * (0.5 + 0.03 * performCard.getHv());
		int roundHp = this.getInt(hpValue);
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		effect.setRoundAtk(-roundAtk);
		effect.setRoundHp(-roundHp);
		effects.add(effect);
		return effects;
	}
}
