package com.bbw.god.game.combat.runes.service.impl.entry.doubletype;

import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 土金 每回合开始时，减少我方场上4张卡牌敌方初始卡组中土、金属性卡牌数*[3]%的攻防值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:53 下午
 */
@Service
public class Runes331308 extends AbstractDoubleTypeRunes {

	private static List<TypeEnum> TYPES = Arrays.asList(TypeEnum.Earth, TypeEnum.Gold);

	@Override
	public int getRunesId() {
		return RunesEnum.TU_JIN_ENTRY.getRunesId();
	}

	@Override
	protected int getTargetCardNum() {
		return 4;
	}

	@Override
	protected double getInitNum() {
		return 0.011;
	}

	@Override
	protected List<TypeEnum> getTypeParam() {
		return TYPES;
	}

	@Override
	protected CardValueEffect makeEffect(BattleCard targetCard, double rate) {
		int deductHp = (int) (targetCard.getHp() * rate);
		int deductAtk = (int) (targetCard.getAtk() * rate);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
		effect.setHp(-deductHp);
		effect.setAtk(-deductAtk);
		return effect;
	}
}
