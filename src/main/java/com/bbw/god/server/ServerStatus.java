package com.bbw.god.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务器状态
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 17:06
 */
@Getter
@AllArgsConstructor
public enum ServerStatus {
	DISABLED("未开启", 0),
	PREDICTING("预告中", 10),
	RUNNING("运行中", 20),
	MAINTAINING("维护中", 30);

	private String name;
	private int value;

	public static ServerStatus fromValue(int value) {
		for (ServerStatus item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return DISABLED;
	}
}
