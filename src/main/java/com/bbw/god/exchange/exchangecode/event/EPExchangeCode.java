package com.bbw.god.exchange.exchangecode.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 兑换礼包事件基础参数
 * @date 2020/3/10 9:33
 */
@Data
public class EPExchangeCode extends BaseEventParam {
	private ExchangeCodeEnum exchangeCodeEnum;

	public EPExchangeCode(ExchangeCodeEnum exchangeCodeEnum, BaseEventParam bep) {
		this.exchangeCodeEnum = exchangeCodeEnum;
		setValues(bep);
	}
}
