package com.bbw.god.game.combat.zhsskill;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 宝鉴（5002）：我方召唤师受到法术攻击时，敌方召唤师受到同样的效果。（混元、死士、噬魂）
 * 对方可以再生效护卫 但是不会再次产生宝鉴
 *
 * @author 作者 ：lwb
 * @version 创建时间：2020年1月13日 下午2:58:18
 * 类说明
 */
@Service
public class BattleSkill5002 extends BattleSkillService {
	private static final int SKILL_ID = 5002;// 技能ID
	private static List<CombatSkillEnum> EFFECT_SKILLS = Arrays.asList(CombatSkillEnum.HY, CombatSkillEnum.SIS, CombatSkillEnum.SHIH, CombatSkillEnum.XI_YANG);

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		if (!psp.receiveSkillEffect() && !psp.getReceiveEffect().isValueEffect()) {
			return ar;
		}
		CardValueEffect reffect = psp.getReceiveEffect().toValueEffect();
		if (reffect.getSourceID() == SKILL_ID) {
			return ar;
		}
		if (!isEffect(reffect.getSourceID())) {
			return ar;
		}
		CardValueEffect effect = CloneUtil.clone(reffect);
		effect.setSourceID(SKILL_ID);
		effect.setTargetPos(psp.getOppoZhsPos());
		effect.setSourcePos(psp.getPerformCard().getPos());
		ar.addEffect(effect);
		AnimationSequence asq = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID,
				psp.getPerformCard().getPos(), psp.getOppoZhsPos());
		ar.addClientAction(asq);
		return ar;
	}

	/**
	 * 是否触发宝鉴
	 *
	 * @param skillId
	 * @return
	 */
	private boolean isEffect(int skillId) {
		return EFFECT_SKILLS.stream().anyMatch(tmp -> tmp.getValue() == skillId);
	}
}

