package com.bbw.god.game.combat.skill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 自愈 每回合恢复自身受到的所有非永久性伤害。
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill3130 extends BattleSkillService {
	private static final int SKILL_ID = 3130;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return attack(psp);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		//自愈 每回合恢复自身受到的所有非永久性伤害。
		Action ar = new Action();
		BattleCard myCard=psp.getPerformCard();
		int hp=myCard.getRoundHp()-myCard.getHp();
		hp = hp > myCard.getRoundHp() ? myCard.getRoundHp() : hp;
		if (hp <= 0) {
			// 没有可以恢复的血量则不发动
			return ar;
		}
		CardValueEffect effect=CardValueEffect.getSkillEffect(SKILL_ID, myCard.getPos());
		effect.setHp(hp);
		effect.setAttackPower(AttackPower.getMaxPower());
		ar.addEffect(effect);
		return ar;
	}
}
