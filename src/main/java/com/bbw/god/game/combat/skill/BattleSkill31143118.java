package com.bbw.god.game.combat.skill;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.config.TypeEnum;

/**
 * 生X 首回合，我方战场上所有X属性卡牌防御力永久上升其本身星级*80，后续每回合防御力永久上升星级*30。每升一阶增加30%的效果。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:31
 */
@Service
public abstract class BattleSkill31143118 extends BattleSkillService {
	/**
	 * 生X
	 */
	public abstract TypeEnum getType();

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 生X 首回合，我方战场上所有X属性卡牌防御力永久上升其本身星级*80，后续每回合防御力永久上升星级*30。每升一阶增加30%的效果。
		TypeEnum matchType = getType();
		Action ar = new Action();
		BattleCard sourceCard = psp.getPerformCard();
		List<BattleCard> playingCards = psp.getMyPlayingCards(true);
		// 同属性的卡
		List<BattleCard> typeCards = playingCards.stream()
				.filter(card -> card.getType().equals(matchType))
				.collect(Collectors.toList());
		if (typeCards.isEmpty()) {
			// 生X 只有上场时才有80倍，所以就算是没有发动成功也需要记录一次使用记录
			psp.getPerformCard().addSkillLog(getMySkillId(),
					psp.getCombat().getRound(), psp.getPerformCard().getPos());
			return ar;
		}
		long effectTimes = sourceCard.getSkillEffectTimes(this.getMySkillId());
		int baseRoundHp = 0 == effectTimes ? 80 : 30;
		int sequence = psp.getNextAnimationSeq();
		for (BattleCard card : typeCards) {
			int rounHp = this.getInt(
					baseRoundHp * card.getStars() * (1 + 0.3 * sourceCard.getHv()));
			CardValueEffect effect = CardValueEffect
					.getSkillEffect(this.getMySkillId(), card.getPos());
			effect.setRoundHp(rounHp);
			effect.setSequence(sequence);
			ar.addEffect(effect);
		}
		return ar;
	}

}
