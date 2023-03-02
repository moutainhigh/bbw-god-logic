package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.stereotype.Service;

/**
 * 玉麒麟
 *
 * @author suhq
 * @date 2018年11月28日 下午5:01:13
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class YuQLProcessor extends TreasureUseProcessor {
	public static final int YuQL_DICE_MAX_ADD = 2400;

	public YuQLProcessor() {
		this.treasureEnum = TreasureEnum.YQL;
		this.isAutoBuy = true;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	@Override
	public boolean isChestType() {
		return true;
	}

	@Override
	public void check(GameUser gu, CPUseTreasure param) {
		if (gu.ifMaxDice(param.getUseTimes())) {
			throw new ExceptionForClientTip("gu.dice.outOfLimit");
		}
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		// 加体力
		ResEventPublisher.pubDiceAddEvent(gu.getId(), TreasureTool.getTreasureConfig().getTreasureEffectYQL(), WayEnum.TREASURE_USE, rd);
	}

}
