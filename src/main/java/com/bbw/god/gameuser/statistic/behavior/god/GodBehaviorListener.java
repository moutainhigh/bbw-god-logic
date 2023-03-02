package com.bbw.god.gameuser.statistic.behavior.god;

import com.bbw.common.DateUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.server.god.AttachNewGodEvent;
import com.bbw.god.server.god.ServerGod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 遇到神仙统计监听类
 * @date 2020/4/23 11:25
 */
@Component
@Slf4j
@Async
public class GodBehaviorListener {
	@Autowired
	private GodStatisticService godStatisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void meetGod(AttachNewGodEvent event) {
		try {
			EventParam<ServerGod> ep = (EventParam<ServerGod>) event.getSource();
			godStatisticService.increment(ep.getGuId(), DateUtil.getTodayInt(), 1);
			GodStatistic godStatistic = godStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), godStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
