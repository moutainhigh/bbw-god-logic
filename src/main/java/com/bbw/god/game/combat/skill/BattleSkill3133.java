package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 吸星 每回合永久吸取对方5%的永久攻防为己用。每升一阶效果+1%。
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill3133 extends BattleSkillService {
	private static final int SKILL_ID = 3133;//技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		//吸星 每回合永久吸取对方5%的永久攻防为己用。每升一阶效果+1%。
		Action ar = new Action();
	    Optional<BattleCard>  opCard=psp.getFaceToFaceCard();
		if (!opCard.isPresent()) {
			return ar;
		}
		return buildEffects(opCard.get(),psp);
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		//吸星 每回合永久吸取对方5%的永久攻防为己用。每升一阶效果+1%。
		Action ar = new Action();
		List<Effect> effects = getEffects(psp.getPerformCard(), target);
		ar.addEffects(effects);
		AnimationSequence asq = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID,
				psp.getPerformCard().getPos(), target.getPos());
		ar.addClientAction(asq);
		return ar;
	}

	/**
	 * 生成效果（未生效）
	 *
	 * @param performCard
	 * @param target
	 * @return
	 */
	public List<Effect> getEffects(BattleCard performCard, BattleCard target) {

		List<Effect> effects = new ArrayList<>();
		int atk = this.getInt((0.05 + 0.01 * performCard.getHv()) * target.getRoundAtk());
		int hp = this.getInt((0.05 + 0.01 * performCard.getHv()) * target.getRoundHp());

		CardValueEffect effect1 = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
		effect1.setRoundHp(-hp);
		effect1.setRoundAtk(-atk);
		effects.add(effect1);

		CardValueEffect effect2 = CardValueEffect.getSkillEffect(SKILL_ID, performCard.getPos());
		effect2.setRoundHp(hp);
		effect2.setRoundAtk(atk);
		effects.add(effect2);
		return effects;
	}

}
