package com.bbw.god.mall.cardshop.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.rd.RDCommon;

/**
 * 卡牌屋事件发布器
 * 
 * @author suhq
 * @date 2019-05-29 09:03:52
 */
public class CardShopEventPublisher {

	public static void pubDrawEndEvent(Long guId, EPDraw ep, RDCommon rd) {
		SpringContextUtil.publishEvent(new DrawEndEvent(new EventParam<EPDraw>(guId, ep, rd)));
	}
}
