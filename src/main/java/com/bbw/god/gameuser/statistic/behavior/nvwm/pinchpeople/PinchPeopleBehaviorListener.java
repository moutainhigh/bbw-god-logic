package com.bbw.god.gameuser.statistic.behavior.nvwm.pinchpeople;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.nightmarenvwam.listener.EPPinchPeople;
import com.bbw.god.gameuser.nightmarenvwam.listener.PinchPeopleEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 女娲庙捏人监听类
 *
 * @author: huanghb
 * @date: 2022/5/20 17:22
 */
@Component
@Slf4j
@Async
public class PinchPeopleBehaviorListener {
	@Autowired
	private PinchPeopleStatistucService statisticService;

	@Order(2)
	@EventListener
	public void pinchPeople(PinchPeopleEvent event) {
		try {
			EPPinchPeople ep = event.getEP();
			long uid = ep.getGuId();
			statisticService.doPinchPeopleStatistic(uid);
			PinchPeopleStatistic pinchPeopleStatistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), pinchPeopleStatistic);
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), pinchPeopleStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
