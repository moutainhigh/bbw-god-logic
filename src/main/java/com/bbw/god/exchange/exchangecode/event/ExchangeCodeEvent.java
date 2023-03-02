package com.bbw.god.exchange.exchangecode.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 兑换礼包事件
 * @date 2020/3/10 9:40
 */
public class ExchangeCodeEvent extends ApplicationEvent implements IEventParam {
	public ExchangeCodeEvent(EPExchangeCode source) {
		super(source);
	}

	@Override
	public EPExchangeCode getEP() {
		return (EPExchangeCode) getSource();
	}
}
