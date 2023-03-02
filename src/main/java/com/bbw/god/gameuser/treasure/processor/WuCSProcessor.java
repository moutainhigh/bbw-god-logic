package com.bbw.god.gameuser.treasure.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.unique.UserMonster;

/**
 * 五彩石
 * 
 * @author suhq
 * @date 2018年11月28日 下午5:00:49
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WuCSProcessor extends TreasureUseProcessor {
	@Autowired
	private GameUserService gameUserService;

	public WuCSProcessor() {
		this.treasureEnum = TreasureEnum.WCS;
		this.isAutoBuy = true;
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		//TODO:可能存在空指针异常gameUserService.getSingleItem
		UserMonster umh = gameUserService.getSingleItem(gu.getId(), UserMonster.class);
		umh.setNextBeatTime(DateUtil.now());
		gameUserService.updateItem(umh);
	}

}
