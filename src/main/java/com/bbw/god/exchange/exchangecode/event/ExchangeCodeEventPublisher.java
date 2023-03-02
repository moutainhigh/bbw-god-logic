package com.bbw.god.exchange.exchangecode.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 礼包事件发布器
 * @date 2020/3/10 9:41
 */
public class ExchangeCodeEventPublisher {
	public static void pubExchangeAwardBagEvent(ExchangeCodeEnum bagEnum, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new ExchangeCodeEvent(new EPExchangeCode(bagEnum, bep)));
	}
}
