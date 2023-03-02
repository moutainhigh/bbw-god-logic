package com.bbw.god.game.config.exchangegood;

import java.util.List;
import java.util.stream.Collectors;

import com.bbw.exception.CoderException;
import com.bbw.god.exchange.ExchangeWayEnum;
import com.bbw.god.game.config.Cfg;

public class ExchangeGoodTool {

	public static CfgExchangeGoodEntity getGood(int exchangeGoodId) {
		CfgExchangeGoodEntity entity = Cfg.I.get(exchangeGoodId, CfgExchangeGoodEntity.class);
		if (entity == null) {
			throw CoderException.high("无效的兑换品" + exchangeGoodId);
		}
		return entity;
	}

	public static List<CfgExchangeGoodEntity> getGoods(ExchangeWayEnum way) {
		return getGoods().stream().filter(tmp -> tmp.getWay() == way.getValue()).collect(Collectors.toList());
	}

	private static List<CfgExchangeGoodEntity> getGoods() {
		return Cfg.I.get(CfgExchangeGoodEntity.class);
	}
}
