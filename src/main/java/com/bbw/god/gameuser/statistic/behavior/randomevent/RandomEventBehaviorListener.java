package com.bbw.god.gameuser.statistic.behavior.randomevent;

import com.bbw.common.DateUtil;
import com.bbw.god.city.yed.EPYeDTrigger;
import com.bbw.god.city.yed.YeDTriggerEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 随机事件统计监听类
 * @date 2020/4/22 16:46
 */
@Component
@Slf4j
@Async
public class RandomEventBehaviorListener {
	@Autowired
	private RandomEventStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void randomEvent(YeDTriggerEvent event) {
		try {
			EventParam<EPYeDTrigger> ep = (EventParam<EPYeDTrigger>) event.getSource();
			EPYeDTrigger epYeDTrigger = ep.getValue();
			statisticService.meetRandomEvent(ep.getGuId(), DateUtil.getTodayInt(), epYeDTrigger.getEvent());
			RandomEventStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
