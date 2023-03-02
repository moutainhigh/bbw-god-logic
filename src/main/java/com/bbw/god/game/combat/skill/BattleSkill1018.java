package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 财神 1018：上场时，本场战斗胜利后所得经验增加10%，每阶增加4%效果，一场战斗触发一次，【财神】可叠加。自带【招财】、【金刚】、【祛毒】。
 * （1）该技能为复合技能。
 * （2）与【招财】类似，该技能的作用对象为战斗胜利后获得召唤师技能。
 * （3）【财神】增加经验的效果可以进行叠加，但是同一张卡牌在一场战斗中只能触发一次该效果。
 * （4）该经验的计算只考虑基础经验，不计算双倍经验丹及经验加倍活动。
 *
 * @author: suhq
 * @date: 2022/1/17 11:27 上午
 */
@Service
public class BattleSkill1018 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.CAI_SHEN.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard performCard = psp.getPerformCard();
		long skillEffectTimes = performCard.getHistotySkillEffectTimes(getMySkillId());
		if (skillEffectTimes >= 1) {
			return ar;
		}
		
		//经验加成
		int expAddRate = 10 + 4 * performCard.getHv();
		Player.Statistics combatStatistics = psp.getPerformPlayer().getStatistics();
		combatStatistics.setCaiShenAddRate(combatStatistics.getCaiShenAddRate() + expAddRate);
		performCard.addSkillLog(getMySkillId(), psp.getCombat().getRound(), performCard.getPos());

		AnimationSequence as = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
		ar.addClientAction(as);
		ar.setTakeEffect(true);
		return ar;
	}
}
