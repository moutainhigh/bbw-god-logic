package com.bbw.god.game.combat.runes.service.impl.entry.doubletype;

import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 火土 每回合开始时，使我方全体卡牌减少敌方初始卡组中火、土属性卡牌数*[3]%的防御值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:53 下午
 */
@Service
public class Runes331307 extends AbstractDoubleTypeRunes {

	private static List<TypeEnum> TYPES = Arrays.asList(TypeEnum.Fire, TypeEnum.Earth);

	@Override
	public int getRunesId() {
		return RunesEnum.HUO_TU_ENTRY.getRunesId();
	}

	@Override
	protected int getTargetCardNum() {
		return 6;
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
		CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
		effect.setHp(-deductHp);
		return effect;
	}
}
