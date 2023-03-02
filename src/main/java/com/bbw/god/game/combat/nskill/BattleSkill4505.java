package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 嘲讽 4505：在场时，该卡牌无视站位必定成为被攻击的目标。
 * （1）该攻击特指物理攻击阶段造成的攻击。
 * （2）嘲讽优先级大于钻地、地劫。当对方有卡牌触发钻地、地劫时，无视其效果的“攻击防御值最低卡牌”，强制攻击发动嘲讽的卡牌。
 * （3）嘲讽的触发时点为当对方卡牌进行攻击时。此时需要弹出嘲讽的触发提示。
 * (4)当我方场上拥有多个嘲讽触发时，以后触发的嘲讽为主。
 *
 * @author: suhq
 * @date: 2021/8/23 2:50 下午
 */
@Service
public class BattleSkill4505 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.CHAO_FENG.getValue();//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		Action ar = new Action();
//		if (psp.getReceiveEffect() == null || !psp.getReceiveEffect().isValueEffect()) {
//			return ar;
//		}
//		CardValueEffect cardValueEffect = psp.getReceiveEffect().toValueEffect();
//		int hp = cardValueEffect.getHp() + cardValueEffect.getRoundHp();
//		if (hp >= 0) {
//			return ar;
//		}
//		List<BattleCard> chaoFengCards = psp.getOppoPlayingCards(SKILL_ID, true);
//		if (ListUtil.isEmpty(chaoFengCards)) {
//			return ar;
//		}
//		BattleCard target = chaoFengCards.get(chaoFengCards.size() - 1);
//		cardValueEffect.setTargetPos(target.getPos());
//		//触发 补充一个动画
//		AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
//		ar.addClientAction(amin);
		return ar;
	}
}
