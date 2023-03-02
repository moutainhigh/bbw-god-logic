package com.bbw.god.activityrank.server.winbox;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.activityrank.server.expedition.ARStepEvent;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;

/**
 * 胜利宝箱事件发送器
 * 
 * @author suhq
 * @date 2019年3月6日 下午3:01:35
 */
public class WinBoxRankEventPublisher {

	/**
	 * 发送战斗开宝箱事件
	 * 
	 * @param guId
	 */
	public static void pubWinBoxEvent(long guId) {
		BaseEventParam bep = new BaseEventParam(guId);
		SpringContextUtil.publishEvent(new ARWinBoxEvent(new EventParam<>(bep, 1)));
	}

}
