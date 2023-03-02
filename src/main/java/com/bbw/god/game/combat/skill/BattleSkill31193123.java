package com.bbw.god.game.combat.skill;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.config.TypeEnum;

/**
 * 强X 首回合，我方战场上所有X属性卡牌攻击力永久上升其本身星级*80，后续每回合攻击力永久上升星级*30。每升一阶增加30%的效果。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:37
 */
@Service
public abstract class BattleSkill31193123 extends BattleSkillService {
	public abstract TypeEnum getType();

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		Action action = attack(psp);
		if (null != action && action.existsEffect()) {
			for (Effect effect : action.getEffects()) {
				if (effect.isValueEffect()) {
					CardValueEffect ve = effect.toValueEffect();
					ve.setRoundAtk(this.getInt(ve.getRoundAtk() * 1.5));
				}
			}
		}
		return action;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 强X 首回合，我方战场上所有X属性卡牌攻击力永久上升其本身星级*80，后续每回合攻击力永久上升星级*30。每升一阶增加30%的效果。
		TypeEnum matchType = getType();
		Action ar = new Action();
		BattleCard sourceCard = psp.getPerformCard();
		List<BattleCard> playingCards = psp.getMyPlayingCards(true);
		// 同属性的卡
		List<BattleCard> typeCards = playingCards.stream()
				.filter(card -> card.getType() == matchType)
				.collect(Collectors.toList());
		if (typeCards.isEmpty()) {
			// 强X 只有上场时才有80倍，所以就算是没有发动成功也需要记录一次使用记录
			psp.getPerformCard().addSkillLog(getMySkillId(),
					psp.getCombat().getRound(), psp.getPerformCard().getPos());
			return ar;
		}
		long effectTimes = sourceCard.getSkillEffectTimes(this.getMySkillId());
		int baseRoundAtk = 0 == effectTimes ? 80 : 30;
		int sequence = psp.getNextAnimationSeq();
		for (BattleCard card : typeCards) {
			int rounAtk = this.getInt(
					baseRoundAtk * card.getStars() * (1 + 0.3 * sourceCard.getHv()));
			CardValueEffect effect = CardValueEffect
					.getSkillEffect(this.getMySkillId(), card.getPos());
			effect.setRoundAtk(rounAtk);
			effect.setSequence(sequence);
			ar.addEffect(effect);
		}
		return ar;
	}

}
