package com.bbw.god.game.combat.runes.service.impl.entry.doubletype;

import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 水木 每回合开始时，破除我方场上3张卡牌敌方初始卡组中水、木属性卡牌数*[3]%的永久攻击值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:53 下午
 */
@Service
public class Runes331305 extends AbstractDoubleTypeRunes {

	private static List<TypeEnum> TYPES = Arrays.asList(TypeEnum.Water, TypeEnum.Wood);

	@Override
	public int getRunesId() {
		return RunesEnum.SHUI_MU_ENTRY.getRunesId();
	}

	@Override
	protected int getTargetCardNum() {
		return 3;
	}

	@Override
	protected double getInitNum() {
		return 0.001;
	}

	@Override
	protected List<TypeEnum> getTypeParam() {
		return TYPES;
	}

	@Override
	protected CardValueEffect makeEffect(BattleCard targetCard, double rate) {
		int deductAck = (int) (targetCard.getRoundAtk() * rate);
		CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
		effect.setRoundAtk(-deductAck);
		return effect;
	}
}
