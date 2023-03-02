package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.nskill.BattleSkill6004;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月19日 上午10:15:25 类说明 死亡技能基础类
 */
public abstract class BattleDieSkill extends BattleSkillService {
	@Autowired
	private BattleSkill6004 battleSkill6004;

	protected abstract Action performSkill(PerformSkillParam psp);

	@Override
	protected Action attack(PerformSkillParam psp) {
		Optional<BattleCard> optional = psp.getEffectSourceCard();
		if (optional.isPresent()) {
			BattleCard card = optional.get();
			Effect effect = psp.getReceiveEffect();
			boolean isNormalAttack = CombatSkillEnum.NORMAL_ATTACK.getValue() == effect.getPerformSkillID();
			boolean isInvalid = isNormalAttack && (card.hasEffect(CombatSkillEnum.SHENJ) || card.hasEffect(CombatSkillEnum.ZHU_XIAN));
			//伤害来源带有技能绝仙
			boolean isHasJueXian = battleSkill6004.isHasJueXian(psp);
			if (isInvalid || isHasJueXian) {
				// 伤害来源卡 带有神剑BUFF 死亡技能失效
				return new Action();
			}
		}
		return performSkill(psp);
	}


}
