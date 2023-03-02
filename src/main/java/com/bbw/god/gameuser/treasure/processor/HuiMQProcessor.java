package com.bbw.god.gameuser.treasure.processor;

import org.springframework.stereotype.Service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;

/**
 * 回马枪
 * 
 * @author suhq
 * @date 2018年11月29日 下午1:39:58
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HuiMQProcessor extends TreasureUseProcessor {

	public HuiMQProcessor() {
		this.treasureEnum = TreasureEnum.HMQ;
		this.isAutoBuy = true;
	}

	@Override
	public void check(GameUser gu, CPUseTreasure param) {
		int dir = param.gainDirection();
		// 客户端传方向检查方向的有效性
		if (dir != 0) {
			CfgRoadEntity road = RoadTool.getRoadById(gu.getLocation().getPosition());
			if (!road.ifHasRoadByDirection(dir)) {
				throw new ExceptionForClientTip("road.not.validDir");
			}
		}

	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		int dir = param.gainDirection();
		if (dir == 0) {
			dir = 5 - gu.getLocation().getDirection();
		}
		gu.moveTo(gu.getLocation().getPosition(), dir);
	}

}
