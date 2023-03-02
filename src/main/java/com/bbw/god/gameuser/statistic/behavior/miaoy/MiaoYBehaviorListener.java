package com.bbw.god.gameuser.statistic.behavior.miaoy;

import com.bbw.common.DateUtil;
import com.bbw.god.city.miaoy.DrawResult;
import com.bbw.god.city.miaoy.EPMiaoYDrawEnd;
import com.bbw.god.city.miaoy.MiaoYDrawEndEvent;
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
 * @description 文王庙抽签统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class MiaoYBehaviorListener {
	@Autowired
	private MiaoYStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(MiaoYDrawEndEvent event) {
		try {
			EventParam<EPMiaoYDrawEnd> ep = (EventParam<EPMiaoYDrawEnd>) event.getSource();
			EPMiaoYDrawEnd drawEnd = ep.getValue();
			DrawResult drawResult = drawEnd.getDrawResult();
			statisticService.draw(ep.getGuId(), DateUtil.getTodayInt(), drawResult);
			MiaoYStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
