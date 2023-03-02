package com.bbw.god.server.god;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 触发神仙
 * 
 * @author suhq
 * @date 2018年10月10日 下午1:45:22
 */
public class GodAttachEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public GodAttachEvent(EventParam<Integer> eventParam) {
		super(eventParam);
	}

}
