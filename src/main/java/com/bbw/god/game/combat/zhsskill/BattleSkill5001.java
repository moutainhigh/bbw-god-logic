package com.bbw.god.game.combat.zhsskill;

import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 护卫
* @author 作者 ：lwb
* @version 创建时间：2020年1月13日 下午2:58:18 
* 类说明 
*/
@Service
public class BattleSkill5001 extends BattleSkillService {

	private static final int SKILL_ID = 5001;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action attackResult = new Action();
		if (!psp.receiveSkillEffect() && !psp.getReceiveEffect().isValueEffect()) {
			return attackResult;
		}
		CardValueEffect reffect = psp.getReceiveEffect().toValueEffect();
		SkillSection cantDefense=SkillSection.getHuWeiCantDefenseBuff();
		if (cantDefense.contains(reffect.getSourceID())){
			return attackResult;
		}
		int hp=reffect.getHp()+reffect.getRoundHp();
		if (hp>=0){
			return attackResult;
		}
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, psp.getPerformCard().getPos());
		effect.setSequence(psp.getNextAnimationSeq());
		effect.setSourcePos(reffect.getSourcePos());
		Double roundHp = hp * (1 - psp.getPerformCard().getHv() * 0.085);
		effect.setRoundHp(this.getInt(roundHp));
		attackResult.addEffect(effect);
		AnimationSequence asq = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID,
				psp.getPerformCard().getPos(), psp.getPerformCard().getPos());
		attackResult.addClientAction(asq);
		return attackResult;
	}
}

