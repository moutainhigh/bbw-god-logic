package com.bbw.god.gameuser.mail.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.mail.UserMail;

public class MailEventPublisher {

	public static void pubMailReceiveEvent(UserMail mail) {
		SpringContextUtil.publishEvent(new ReceiveMailEvent(new EventParam<>(mail.getReceiverId(), mail)));
	}
}
