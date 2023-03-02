package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * 烈焰  每回合，对敌方地面卡牌施放【炙焰】，无视回光。
 * @author liuwenbin
 */
@Service
public class BattleSkill3158 extends BattleSkillService {
	private static final int SKILL_ID = 3158;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
	@Autowired
	private BattleSkill3159 battleSkill3159;
	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cardList=psp.getOppoPlayingCards(false);
		int seq=psp.getNextAnimationSeq();
		for (BattleCard battleCard : cardList) {
			if (battleCard!=null){
				ar.addEffect(battleSkill3159.getEffect(seq,psp.getPerformCard(),battleCard.getPos()));
			}
		}
		return ar;
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

