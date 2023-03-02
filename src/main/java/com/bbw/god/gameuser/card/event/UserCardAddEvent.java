package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 2018年10月15日 上午9:33:27
 */
public class UserCardAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardAddEvent(EPCardAdd source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardAdd getEP() {
		return (EPCardAdd) getSource();
	}

}
