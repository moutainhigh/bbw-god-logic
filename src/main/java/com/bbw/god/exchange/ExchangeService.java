package com.bbw.god.exchange;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.game.config.exchangegood.ExchangeGoodTool;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;

/**
 * 兑换
 * 
 * @author suhq
 * @date 2018年12月26日 上午10:06:48
 */
@Service
public abstract class ExchangeService {
	protected ExchangeWayEnum exchangeWay;

	/**
	 * 可兑换的物品
	 * 
	 * @param type
	 * @return
	 */
	public RDExchangeList getExchangeableGoods(long guId) {
		List<CfgExchangeGoodEntity> goods = ExchangeGoodTool.getGoods(exchangeWay);
		// 转换为客户端数据
		return toRdExchangeList(guId, goods);
	}

	abstract RDExchangeList toRdExchangeList(long guId, List<CfgExchangeGoodEntity> goods);

	/**
	 * 兑换物品
	 * 
	 * @param guId
	 * @param goodId
	 * @param num
	 * @return
	 */
	public RDCommon exchange(long guId, int sid, int exchangeGoodId, int exchangeNum) {

		// 数量有效性
		if (exchangeNum <= 0) {
			throw new ExceptionForClientTip("exchange.not.valid.num");
		}
		// 物品有效性
		CfgExchangeGoodEntity exchangeGood = ExchangeGoodTool.getGood(exchangeGoodId);
		if (exchangeGood == null || !exchangeGood.getIsValid()) {
			throw new ExceptionForClientTip("exchange.not.valid");
		}
		return toDeliver(guId, sid, exchangeGood, exchangeNum);
	}

	abstract RDCommon toDeliver(long guId, int sid, CfgExchangeGoodEntity exchangeGood, int exchangeNum);

	/**
	 * 发放物品
	 * 
	 * @param guId
	 * @param exchangeNum
	 * @param exchangeGood
	 * @param way
	 * @param rd
	 */
	protected void deliver(Long guId, CfgExchangeGoodEntity exchangeGood, int exchangeNum, WayEnum way, RDCommon rd) {
		AwardEnum award = AwardEnum.fromValue(exchangeGood.getType());
		int num = exchangeNum * exchangeGood.getNum();
		switch (award) {
		case YB:
			ResEventPublisher.pubGoldAddEvent(guId, num, way, rd);
			break;
		case TQ:
			ResEventPublisher.pubCopperAddEvent(guId, num, way, rd);
			break;
		case TL:
			ResEventPublisher.pubDiceAddEvent(guId, num, way, rd);
			break;
		case KP:
			CardEventPublisher.pubCardAddEvent(guId, exchangeGood.getGoodId(), way, "在" + way.getName(), rd);
			break;
		case YS:
			ResEventPublisher.pubEleAddEvent(guId, exchangeGood.getGoodId(), num, way, rd);
			break;
		case FB:
			TreasureEventPublisher.pubTAddEvent(guId, exchangeGood.getGoodId(), num, way, rd);
			break;
		default:
			break;
		}
	}
}
