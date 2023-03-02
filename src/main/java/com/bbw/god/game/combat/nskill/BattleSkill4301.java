package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 4301 奇袭 可率先进行攻击，如击退敌方卡牌则我方卡牌不损防御。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 11:14
 */
@Service
public class BattleSkill4301 extends BattleNormalAttack {
	private static final int SKILL_ID = CombatSkillEnum.QX.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		Optional<BattleCard> faceCardObj = psp.getFaceToFaceCard();
		// 对面没有卡牌
		if (!faceCardObj.isPresent()) {
			ar.setTakeEffect(false);
			return ar;
		}
		// 物理攻击
		AnimationSequence asq = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID,
				psp.getPerformCard().getPos());
		ar.addClientAction(asq);
		getNormalAttackEffect(psp, ar);
		return ar;
	}
}
