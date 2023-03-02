package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.AttackServiceFactory;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 刚毅: 威风、妖术、魅惑技能对其无效且反弹影响到施法术卡牌。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill3002 implements ISkillDefenseService {
	private static final int SKILL_ID = 3002;
	@Autowired
	private AttackServiceFactory serviceFactory;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action action = new Action();
		//如果不是技能伤害，则不发动技能
		if (!psp.receiveSkillEffect()) {
			return action;
		}
		// 不在防守技能列表中
		if (!this.contains(BattleSkillDefenseTableService.getDefenseTableBySkillId(SKILL_ID), psp.getReceiveEffectSkillId())) {
			return action;
		}
		//威风、妖术、魅惑技能对其无效且反弹影响到施法术卡牌。
		//生成一个反弹自身的不可防御技能效果
		int targetPos=psp.getReceiveEffect().getSourcePos();
		int skillId=psp.getReceiveEffectSkillId();
		if (psp.getReceiveEffect().isFromGroupSkill()) {
			if (psp.getReceiveEffect().getSourcePos()<10) {
				List<BattleCard> cards=psp.getOppoPlayer().getPlayingCardsByGroupSkillId(psp.getReceiveEffectSkillId());
				BattleCard target=PowerRandom.getRandomFromList(cards);
				targetPos=target.getPos();
			}
			//如果收到的是四海龙王技能则返回威风效果（四海龙王造成的效果是威风）
			if (skillId== CombatSkillEnum.SHLW_G.getValue()) {
				skillId=CombatSkillEnum.WF.getValue();
			}
		}
		BattleSkillService service = serviceFactory.getSkillAttackService(skillId);
		psp.getReceiveEffect().setSourceID(skillId);
		List<Effect> effects=service.attackTargetPosByCopyEffects(psp,targetPos);
		action.addEffects(effects);
		//清除作用自身的技能效果
		psp.getReceiveEffect().setDefended(true);
		psp.setReceiveEffect(null);
		return action;
	}
}
