package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 3103	治愈	每回合回复我方召唤师至少350点血量，每升一级最高可能回复量+100，每升一阶增加40%的治疗效果。。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 00:09
 */
@Service
public class BattleSkill3103 extends BattleSkillService {
	private static final int SKILL_ID = 3103;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 3103	治愈	每回合回复我方召唤师至少350点血量，每升一级最高可能回复量+100。
		Player me = psp.getPerformPlayer();
		if (me.getHp() >= me.getMaxHp()) {
			return ar;
		}
		BattleCard performCard = psp.getPerformCard();
		CardValueEffect effect = buildEffect(me, performCard.getLv(), performCard.getHv(), psp.getNextAnimationSeq());
		ar.addEffect(effect);
		return ar;
	}

	public CardValueEffect buildEffect(Player performPlayer, int lv, int hv, int animationSeq) {
		int playerPos = PositionService.getZhaoHuanShiPos(performPlayer.getId());
		int damage = performPlayer.getMaxHp() - performPlayer.getHp();//已损伤害
		//攻击目标卡牌
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, playerPos);
		int min = 350;
		int max = min + 100 * lv;
		int hp = PowerRandom.getRandomBetween(min, max);
		hp = getInt(hp * (1 + 0.4 * hv));
		effect.setHp(hp > damage ? damage : hp);//不能过度治疗
		effect.setSequence(animationSeq);
		return effect;
	}
}
