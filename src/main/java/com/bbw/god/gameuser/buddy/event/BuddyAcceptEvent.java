package com.bbw.god.gameuser.buddy.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 好友申请同意事件
 * 
 * @author suhq
 * @date 2019-05-23 17:08:59
 */
public class BuddyAcceptEvent extends ApplicationEvent {

	public BuddyAcceptEvent(EventParam<Long> source) {
		super(source);
	}

}
