package com.bbw.god.activity.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 邀请达成事件
 * 
 * @author suhq
 * @date 2019年3月6日 下午2:59:59
 */
public class AInviteSuccessEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public AInviteSuccessEvent(EventParam<Integer> source) {
		super(source);
	}

}
