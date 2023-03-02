package com.bbw.god.chat;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-23 16:05
 */
@Data
public class ChatRequestResult {
	private int res = -1;
	private String message;

	public boolean success() {
		return 0 == res;
	}
}
