package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 长生：死亡时，立刻满状态回到场上（原地），仅限一次。进入坟地后被拉回可重置该技能。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill1205 extends BattleDieSkill {
	private static final int SKILL_ID = 1205;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action performSkill(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard card = psp.getPerformCard();
		int hp = card.getInitHp() - card.getHp() + BattleCardService.posGainHp(card);
		int atk = card.getInitAtk() - card.getAtk() + BattleCardService.posGainAtk(card);
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
		effect.setRoundAtk(atk);
		effect.setRoundHp(hp);
		if (card.getNormalAttackSkill().getTimesLimit().getBanFrom()>0){
			card.getNormalAttackSkill().getTimesLimit().reset();
		}
		card.resetBuffStatus(false);
		card.setAlive(true);
		// 将 回合血量和攻击设置成 现有的血量和攻击，防止 hp和atk 加错
		card.setRoundHp(card.getHp());
		card.setRoundAtk(card.getAtk());
		ar.addEffect(effect);
		psp.setReceiveEffect(null);
		return ar;

	}
}
