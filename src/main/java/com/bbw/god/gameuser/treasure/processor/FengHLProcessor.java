package com.bbw.god.gameuser.treasure.processor;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.treasure.TreasureEnum;

/**
 * 风火轮
 * 
 * @author suhq
 * @date 2018年11月28日 下午5:04:09
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FengHLProcessor extends ShanHSJTProcessor {
	public FengHLProcessor() {
		this.treasureEnum = TreasureEnum.FHL;
		this.isAutoBuy = false;
	}
}
