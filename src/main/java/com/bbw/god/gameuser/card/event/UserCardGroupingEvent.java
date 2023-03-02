package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 卡牌编组事件
 * 
 * @author suhq
 * @date 2018年11月6日 下午3:08:02
 */
public class UserCardGroupingEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardGroupingEvent(EPCardGrouping eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardGrouping getEP() {
		return (EPCardGrouping) getSource();
	}

}
