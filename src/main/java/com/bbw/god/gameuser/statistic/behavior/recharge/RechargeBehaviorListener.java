package com.bbw.god.gameuser.statistic.behavior.recharge;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.pay.DeliverNotifyEvent;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 充值统计监听类(同步监听)
 * @date 2020/7/3 15:20
 */
@Component
@Slf4j
public class RechargeBehaviorListener {
	@Autowired
	private RechargeStatisticService rechargeStatisticService;

	@Order(1)
	@EventListener
	public void recharge(DeliverNotifyEvent event) {
		try {
			UserReceipt userReceipt = event.getParam();
			Long uid = userReceipt.getGameUserId();
			Integer price = userReceipt.getPrice();
			rechargeStatisticService.recharge(uid, DateUtil.toDateInt(userReceipt.getDeliveryTime()), price);
			RechargeStatistic statistic = rechargeStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.NONE, new RDCommon(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
