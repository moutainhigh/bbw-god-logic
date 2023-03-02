package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 2019-05-24 09:51:17
 */
public class UserCardDelEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardDelEvent(EPCardDel source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardDel getEP() {
		return (EPCardDel) getSource();
	}

}
