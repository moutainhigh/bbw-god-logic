package com.bbw.god.mall.lottery.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 奖券奖励发放事件
 * @date 2020/7/15 16:29
 **/
public class LotteryAwardSendEvent extends ApplicationEvent implements IEventParam {

	public LotteryAwardSendEvent(EPLotteryAwardSend source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPLotteryAwardSend getEP() {
		return (EPLotteryAwardSend) getSource();
	}
}
