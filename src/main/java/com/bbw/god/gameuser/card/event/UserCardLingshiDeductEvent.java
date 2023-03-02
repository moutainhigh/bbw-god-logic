package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 消耗卡牌碎片
 *
 */
public class UserCardLingshiDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardLingshiDeductEvent(EPCardLingShi source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardLingShi getEP() {
		return (EPCardLingShi) getSource();
	}

}
