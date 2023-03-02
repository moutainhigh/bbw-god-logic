package com.bbw.god.gameuser.statistic.behavior.snatchtreasure;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.mall.snatchtreasure.event.EPSnatchTreasureDraw;
import com.bbw.god.mall.snatchtreasure.event.SnatchTreasureDrawEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 夺宝统计监听类
 * @date 2020/6/30 14:47
 */
@Component
@Slf4j
@Async
public class SnatchTreasureBehaviorListener {
	@Autowired
	private SnatchTreasureStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(SnatchTreasureDrawEvent event) {
		try {
			EPSnatchTreasureDraw ep = event.getEP();
			statisticService.draw(ep.getGuId(), DateUtil.getTodayInt(), ep.getDrawTimes());
			SnatchTreasureStatistic statistic = statisticService.fromRedis(ep.getGuId(),
					StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubSnatchBehaviorEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
