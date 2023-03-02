package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *3146 反间 挑拨敌方两张卡牌互相攻击，无视回光，如无可攻击目标，则正常攻击对位。
 */
@Service
public class BattleSkill3146 extends BattleSkillService {
	private static final int SKILL_ID = 3146;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//挑拨敌方两张卡牌互相攻击
		List<BattleCard> cards=psp.getOppoPlayingCards(false);
		if (ListUtil.isEmpty(cards) || cards.size()<2) {
			return ar;
		}
		int sequence = psp.getNextAnimationSeq();
		List<BattleCard> targetCards=PowerRandom.getRandomsFromList(2,cards);
		BattleSkillEffect effect1 = getEffect(sequence, targetCards.get(0), targetCards.get(0).getPos());
		BattleSkillEffect effect2 = getEffect(sequence, targetCards.get(1), targetCards.get(1).getPos());
		ar.addEffect(effect1);
		ar.addEffect(effect2);
		return ar;
	}

	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp,int attkPos) {
		BattleCard myCard =fromPsp.getCombat().getBattleCard(attkPos);
		int sequence = fromPsp.getNextAnimationSeq();
		List<Effect> effects=new ArrayList<>();
		BattleSkillEffect effect=getEffect(sequence, myCard, attkPos);
		//如果被反弹则视为释放者自己中了挑拨（随机攻击）
		effect.setSourceID(CombatSkillEnum.TIAO_BO.getValue());
		effects.add(effect);
		return effects;
	}

	private BattleSkillEffect getEffect(int sequence,BattleCard myCard,int attkPos){
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, attkPos);
		//限制使用技能
		for (BattleSkill skill : myCard.get31013199Skills()) {
			TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
			effect.forbidOneRound(skill.getId(), limt,getMySkillId());
		}
		effect.changeSkillAttackTarget(CombatSkillEnum.NORMAL_ATTACK.getValue(), CombatSkillEnum.TIAO_BO.getValue());
		effect.setSequence(sequence);
		return effect;
	}

}