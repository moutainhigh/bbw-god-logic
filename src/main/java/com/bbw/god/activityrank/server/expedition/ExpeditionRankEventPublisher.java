package com.bbw.god.activityrank.server.expedition;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;

/**
 * 远征事件发送器
 * 
 * @author suhq
 * @date 2019年3月6日 下午3:01:35
 */
public class ExpeditionRankEventPublisher {

	/**
	 * 发送行走步数事件
	 * 
	 * @param guId
	 * @param step
	 */
	public static void pubStepEvent(long guId, int step) {
		BaseEventParam bep = new BaseEventParam(guId);
		SpringContextUtil.publishEvent(new ARStepEvent(new EventParam<>(bep, step)));
	}

}
