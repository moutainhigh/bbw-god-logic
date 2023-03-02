package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.AttackServiceFactory;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 属性克制卡牌，攻击力提高50%
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-15 16:05
 */
@Service
public abstract class RestrainBattleSkill extends BattleSkillService {
	@Autowired
	private AttackServiceFactory attackServiceFactory;
	/**
	 * 克制的属性
	 * @return
	 */
	public abstract TypeEnum getRestrainType();

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//对位卡牌
		Optional<BattleCard> targetCard = psp.getFaceToFaceCard();
		//对位卡不存在 或者 释放卡无法发动普攻时 不触发属性克制
		List<BattleSkill> skills=psp.getPerformCard().getEffectiveSkills(SkillSection.getNormalAttackSection());
		if (!targetCard.isPresent() || skills.isEmpty()) {
			return ar;
		}
		if (!psp.getPerformCard().hasEffectiveSkill(CombatSkillEnum.TJ.getValue()) && targetCard.get().getType() != getRestrainType()) {
			return ar;
		}
		if (targetCard.get().hasEffectiveSkill(CombatSkillEnum.TJ.getValue())){
			//对方有太极 则不发动属性克制
			return ar;
		}
		//面对属性克制卡牌，攻击力提高50%
		BattleCard sourceCard = psp.getPerformCard();
		if (sourceCard.hasEffectiveSkill(CombatSkillEnum.KD.getValue())){
			return attackServiceFactory.getSkillAttackService(CombatSkillEnum.KD.getValue()).productAction(psp);
		}
		int atk = this.getInt(sourceCard.getAtk() * 0.5);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), sourceCard.getPos());
		effect.setAtk(atk);
		effect.setValueType(CardValueEffectType.DELAY);
		ar.addEffect(effect);
		return ar;
	}
}
