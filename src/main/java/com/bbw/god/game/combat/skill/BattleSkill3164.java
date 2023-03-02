package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玄幻 3164：每回合，施放1次【幻术】，召唤的卡牌拥有与持有卡牌相同的技能，10阶可额外召唤1张。
 * （1）随机规则与幻术一致。
 * （2）随机到的卡牌若玩家已拥有，则按照玩家该卡牌所配置的技能、符箓，进行生成。
 *
 * @author: suhq
 * @date: 2021/9/25 6:13 上午
 */
@Service
public class BattleSkill3164 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.XUAN_HUAN.getValue();// 技能ID
	@Autowired
	private BattleSkill3142 battleSkill3142;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int seq = psp.getNextAnimationSeq();
		//文字动画要在召唤动画前面
		ar.addClientAction(ClientAnimationService.getSkillAction(seq, getMySkillId(), psp.getPerformCard().getPos()));
		BattleCard sumonCard1 = battleSkill3142.doSummonCard(ar, psp, true);
		if (null == sumonCard1) {
			ar.getClientActions().clear();
			return ar;
		}

		// 第二次
		if (sumonCard1 != null && psp.getPerformCard().getHv() == 10) {
			battleSkill3142.doSummonCard(ar, psp, true);
		}
		return ar;
	}
}
