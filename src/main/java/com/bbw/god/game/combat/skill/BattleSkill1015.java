package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 混绫：上场时对敌方场上所有卡牌施加一次【枷锁】，无视回光，对云台有效。
 * 
 */
@Service
public class BattleSkill1015 extends BattleSkillService {
	private static final int SKILL_ID = 1015;// 技能ID

	@Autowired
	private BattleSkill3112 battleSkill3112;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> battleCards = psp.getOppoPlayingCards(true);
		int seq=psp.getNextAnimationSeq();
		for (BattleCard card : battleCards) {
			BattleSkillEffect effect = battleSkill3112.getEffect(card);
			effect.replaceEffectSkillId(getMySkillId());
			effect.setSourcePos(psp.getPerformCard().getPos());
			effect.setSequence(seq);
			ar.addEffect(effect);
		}
		return ar;
	}
}
