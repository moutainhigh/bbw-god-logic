package com.bbw.god.game.combat.nskill;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus.StatusEffectType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 神剑：无视灵动，且被该卡攻击击退的卡牌复活技能无法生效。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 05:14
 */
@Service
public class BattleSkill4114 extends BattleSkillService {
	private static final int SKILL_ID = 4114;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard card = psp.getPerformCard();
		Optional<BattleCard> op = psp.getFaceToFaceCard();
		if (!op.isPresent() && !psp.getPerformCard().hasZuanDiSkill()) {
			return ar;
		}
		BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(StatusEffectType.NORMAL_ATTACK, SKILL_ID,
				card.getPos());
		effect.setSouceCard(CloneUtil.clone(psp.getPerformCard()));
		ar.addEffect(effect);
		return ar;
	}
}
