package com.bbw.god.game.combat.skill;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 道法 只要其在场上，我方战场上所有其它卡牌攻防各加500，每阶再加30点。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:56
 */
@Service
public class BattleSkill3126 extends BattleSkillService {
	private static final int SKILL_ID = 3126;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 道法 只要其在场上，我方战场上所有其它卡牌攻防各加500，每阶再加30点。
		Action attackResult = new Action();
		List<BattleCard> playingCards = psp.getMyPlayingCards(true);
		int atk = 500;
		atk += 30 * psp.getPerformCard().getHv();
		int hp = atk;
		int sequence = psp.getNextAnimationSeq();
		int perfromCardPos = psp.getPerformCard().getPos();
		for (BattleCard card : playingCards) {
			if (perfromCardPos == card.getPos()) {
				// 当前释放技能的卡牌不享受该效果
				continue;
			}
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setHp(hp);
			effect.setAtk(atk);
			effect.setSequence(sequence);
			attackResult.addEffect(effect);
		}
		return attackResult;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return attack(psp);
	}

}
