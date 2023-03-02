package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 *1.炙焰  每回合，破除敌方地面一张卡牌150点永久攻防,每阶增加50%效果。
 * （1）处于军师位时的收益与火球相同。
 * （2）不会选择云台。
 * （3）减少的是永久攻防，金刚无法抵挡。
 *
 * @author liuwenbin
 */
@Service
public class BattleSkill3159 extends BattleSkillService {
	private static final int SKILL_ID = 3159;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cardList=psp.getOppoPlayingCards(false);
		if (ListUtil.isEmpty(cardList)){
			return ar;
		}
		int seq=psp.getNextAnimationSeq();
		BattleCard random = PowerRandom.getRandomFromList(cardList);
		ar.addEffect(getEffect(seq,psp.getPerformCard(),random.getPos()));
		return ar;
	}
	
	public CardValueEffect getEffect(int seq,BattleCard performCard,int toPos){
		int val=-getInt(150*(1+performCard.getHv()*0.5));
		CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getMySkillId(),toPos);
		valueEffect.setSequence(seq);
		valueEffect.setRoundHp(val);
		valueEffect.setRoundAtk(val);
		return valueEffect;
	}
	

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		Action action = attack(psp);
		if (null != action && action.existsEffect()) {
			for (Effect effect : action.getEffects()) {
				if (effect.isValueEffect()) {
					CardValueEffect ve = effect.toValueEffect();
					ve.setHp(this.getInt(ve.getHp() * 1.5));
					ve.setRoundHp(this.getInt(ve.getRoundHp() * 1.5));
					ve.setAtk(this.getInt(ve.getAtk() * 1.5));
					ve.setRoundAtk(this.getInt(ve.getRoundAtk() * 1.5));
				}
			}
		}
		return action;
	}
}
