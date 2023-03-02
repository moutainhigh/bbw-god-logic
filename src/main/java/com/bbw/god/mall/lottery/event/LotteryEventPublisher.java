package com.bbw.god.mall.lottery.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 奖券事件发送器
 * @date 2020/7/15 16:37
 **/
public class LotteryEventPublisher {
	/**
	 * 奖励发送事件
	 *
	 * @param group
	 * @param bep
	 */
	public static void pubLotteryAwardSendEvent(Integer group, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new LotteryAwardSendEvent(new EPLotteryAwardSend(group, bep)));
	}
}
