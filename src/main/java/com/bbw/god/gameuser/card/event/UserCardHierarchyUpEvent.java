package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 2018年10月9日 下午2:49:33
 */
public class UserCardHierarchyUpEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardHierarchyUpEvent(EPCardHierarchyUp eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardHierarchyUp getEP() {
		return (EPCardHierarchyUp) this.getSource();
	}
}
