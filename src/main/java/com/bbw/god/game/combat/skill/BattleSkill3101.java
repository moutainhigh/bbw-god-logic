package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 3101	飞狙	每回合破除敌方随机1张卡牌100点永久防御，每升一阶增长50%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 23:10
 */
@Service
public class BattleSkill3101 extends BattleSkillService {
	private static final int SKILL_ID = 3101;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 3101	飞狙	每回合破除敌方随机1张卡牌100点永久防御，每升一阶增长50%的效果。
		Optional<BattleCard> rndOppoCard = psp.randomOppoPlayingCard(true);
		if (!rndOppoCard.isPresent()) {
			return new Action();
		}
		BattleCard targetCard = rndOppoCard.get();
		return buildEffects(targetCard,psp);
	}

	/**
	 * 获取攻击的血量
	 * @param performCard
	 * @return
	 */
	public int getAttackHp(int hv){
		Double roundHp=100 * (1 + hv* 0.5);
		return roundHp.intValue();
	}

	@Override
	public Action buildEffects(BattleCard target,PerformSkillParam psp) {
		Action ar = new Action();
		// 3101	飞狙	每回合破除敌方随机1张卡牌100点永久防御，每升一阶增长50%的效果。
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, target.getPos());
		int roundHp = getAttackHp(psp.getPerformCard().getHv());
		effect.setRoundHp(-roundHp);
		effect.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(effect);
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(), psp.getPerformCard().getPos(),target.getPos()));
		return ar;
	}
}
