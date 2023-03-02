package com.bbw.god.gameuser.chamberofcommerce.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 购买商会礼包事件
 *
 * @author suchaobin
 * @date 2020-02-21
 */
public class BuyCocBagEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public BuyCocBagEvent(EPBuyCocBag source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPBuyCocBag getEP() {
		return (EPBuyCocBag) getSource();
	}
}
