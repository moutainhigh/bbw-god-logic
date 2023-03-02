package com.bbw.god.gameuser.mail.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.mail.UserMail;

/**
 * 邮件接收事件
 * 
 * @author suhq
 * @date 2019-08-20 10:26:50
 */
public class ReceiveMailEvent extends ApplicationEvent {

	public ReceiveMailEvent(EventParam<UserMail> source) {
		super(source);
	}

}
