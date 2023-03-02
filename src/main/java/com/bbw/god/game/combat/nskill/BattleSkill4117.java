package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 属性克制敌方时，攻击额外提高50%。（可被无相克制）
 * 即 在原有攻击翻1.25倍
 */
@Service
public class BattleSkill4117 extends BattleSkillService {
	private static final int SKILL_ID = 4117;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard sourceCard=psp.getPerformCard();
		int atk = this.getInt(sourceCard.getAtk()*1.25);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), sourceCard.getPos());
		effect.setAtk(atk);
		effect.setValueType(CardValueEffect.CardValueEffectType.DELAY);
		ar.addEffect(effect);
		AnimationSequence as= ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),sourceCard.getPos());
		ar.addClientAction(as);
		return ar;
	}
}
