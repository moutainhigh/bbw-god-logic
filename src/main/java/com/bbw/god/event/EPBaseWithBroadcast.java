package com.bbw.god.event;

import lombok.Getter;
import lombok.Setter;

/**
 * 带触发广播的条件信息的事件参数
 * 
 * @author suhq
 * @date 2019-10-15 10:06:08
 */
@Getter
@Setter
public class EPBaseWithBroadcast extends BaseEventParam {

	private String broadcastWayInfo = "";// 触发广播的条件信息

	public EPBaseWithBroadcast() {

	}
}
