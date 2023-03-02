package com.bbw.god.activityrank.server.recharge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.pay.DeliverNotifyEvent;

/**
 * 充值榜
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class RechargeRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.RECHARGE_RANK;
	@Autowired
	private ActivityRankService activityRankService;

	/**
	 * 产品发放通知事件，处理跟充值有关的活动
	 * 
	 * @param event
	 */
	@EventListener
	@Order(1000)
	public void deliverNotify(DeliverNotifyEvent event) {
		UserReceipt userReceipt = event.getParam();
		long guId = userReceipt.getGameUserId();
		// 充值榜,以充值金额*10 当作数量算入充值排行榜
		activityRankService.incrementRankValue(guId, userReceipt.getPrice(), rankType);
	}

}
