package com.bbw.god.gameuser.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件类型
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 12:23
 */
@Getter
@AllArgsConstructor
public enum MailType {
	PLAYER("个人邮件", 10), SYSTEM("系统邮件", 20), AWARD("领奖中心", 30);

	private String name;
	private int value;

	public static MailType fromValue(int value) {
		for (MailType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}