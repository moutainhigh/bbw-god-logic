package com.bbw.mc.mail;

import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.Person;

import lombok.Getter;

/**
 * 邮件信息
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-25 17:10
 */
@Getter
public class MailMsg extends Msg {
	private String title;

	public MailMsg(Person person, String title, String content) {
		this.type = MsgType.MAIL;
		this.person = person;
		this.title = title;
		this.content = content;
	}

}
