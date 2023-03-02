package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.AttackServiceFactory;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 3202 反射 自身受到法术攻击，则施法者受到双倍的效果，位于云台位时伤害提高50%。
 *
 */
@Service
public class BattleSkill3202 extends BattleSkillService {
	private static final int SKILL_ID = 3202;// 技能ID
	@Autowired
	private AttackServiceFactory serviceFactory;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}
	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		// 回光在军师位需要有1.2倍加成，在云台位有1.5倍加成。
		Action action = attack(psp);
		if (null != action && action.existsEffect()) {
			for (Effect effect : action.getEffects()) {
				if (effect.isValueEffect()) {
					CardValueEffect ve = effect.toValueEffect();
					ve.setHp(this.getInt(ve.getHp() * 1.2));
					ve.setRoundHp(this.getInt(ve.getRoundHp() * 1.2));
				}
			}
		}
		return action;
	}

	private Action yunTaiBuffAction(Action action) {
		// 位于云台位时伤害提高50%。
		if (null != action && action.existsEffect()) {
			for (Effect effect : action.getEffects()) {
				if (effect.isValueEffect()) {
					CardValueEffect ve = effect.toValueEffect();
					ve.setHp(this.getInt(ve.getHp() * 1.5));
					ve.setRoundHp(this.getInt(ve.getRoundHp() * 1.5));
				}
			}
		}
		return action;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		// 如果收到技能伤害，则不发动技能
		if (!psp.receiveSkillEffect()) {
			return action;
		}
		if (psp.getReceiveEffect().getSourcePos() == psp.getReceiveEffect()
				.getTargetPos()) {
			return action;
		}
		int receiveSkillId = psp.getReceiveEffectSkillId();
		// 不在防守技能列表中 或者受到的技能等级大于1，如果不校对防御等级的话 可能导致多次回光
		if (!this.contains(BattleSkillDefenseTableService.getDefenseTableBySkillId(SKILL_ID), receiveSkillId)
				|| psp.getReceiveEffect().getAttackPower().getValue() > AttackPower.L1.getValue()) {
			return action;
		}
		int skillId = receiveSkillId == 2012 ? 3107 : receiveSkillId;
		// 生成一个反弹自身的不可防御技能效果
		BattleSkillService service = serviceFactory
				.getSkillAttackService(skillId);
		List<Effect> effects = service.attackTargetPosByCopyEffects(psp,
				psp.getReceiveEffect().getSourcePos());
		for (Effect effect : effects) {
			effect.setPerformSkillID(SKILL_ID);
			effect.setSourcePos(psp.getPerformCard().getPos());
			if (effect.isValueEffect()){
				CardValueEffect ve = effect.toValueEffect();
				ve.setHp(ve.getHp() * 2);
				ve.setRoundHp(ve.getRoundHp() * 2);
			}
		}
		action.addEffects(effects);
		if (PositionService.isYunTaiPos(psp.getPerformCard().getPos())) {
			return yunTaiBuffAction(action);
		}
		return action;
	}
}
