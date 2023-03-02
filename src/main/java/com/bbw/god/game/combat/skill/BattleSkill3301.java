package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 坚毅：受到普通攻击或技能效果时，提高星级*120点永久防御值，每阶增加40%效果。
 * （1）若卡牌在受到普通攻击或技能效果导致该卡牌死亡或离场，则不会触发该技能。
 *
 * @author: suhq
 * @date: 2022/5/9 10:37 上午
 */
@Service
public class BattleSkill3301 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.JIAN_YI.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard performCard = psp.getPerformCard();
		PositionType positionType = PositionService.getPositionType(performCard.getPos());
		if (PositionType.BATTLE != positionType || performCard.isKilled()) {
			return ar;
		}
		Effect receiveEffect = psp.getReceiveEffect();
		if (receiveEffect instanceof CardValueEffect) {
			CardValueEffect effect = (CardValueEffect) receiveEffect;
			int effectHp = effect.getRoundHp() + effect.getHp();
			if (effectHp == 0) {
				return ar;
			}
			boolean isToDie = effectHp + performCard.getHp() <= 0;
			if (isToDie) {
				return ar;
			}
		}

		CardValueEffect selfEffeck = CardValueEffect.getSkillEffect(getMySkillId(), performCard.getPos());
		Double roundHp = 120 * psp.getPerformCard().getStars() * (1 + 0.4 * psp.getPerformCard().getHv());
		selfEffeck.setRoundHp(this.getInt(roundHp));
		selfEffeck.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(selfEffeck);
		return ar;
	}
}
