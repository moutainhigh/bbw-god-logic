package com.bbw.god.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-29 11:00
 */
@Getter
@AllArgsConstructor
public enum ServerGroup {
	IOS(10), // IOS
	AD(16), // 买量
	ANDRIOD(20), // 安卓渠道
	IOS_ZHAO_HUAN_SHI(40), // 安卓渠道
	MAOER(110);// 猫耳专服
	private final int group;

	public static ServerGroup fromGroup(int value) {
		for (ServerGroup item : values()) {
			if (item.getGroup() == value) {
				return item;
			}
		}
		return ServerGroup.IOS;
	}
}
