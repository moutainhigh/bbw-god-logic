package com.bbw.god.gameuser.statistic.behavior.nvwm;

import com.bbw.common.DateUtil;
import com.bbw.god.city.nvwm.EPNvWMDonate;
import com.bbw.god.city.nvwm.NwmDonateEvent;
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
 * @description 女娲庙监听类
 * @date 2020/4/23 9:21
 */
@Component
@Slf4j
@Async
public class NvwmBehaviorListener {
	@Autowired
	private NvwmStatisticService statisticService;

	@Order(2)
	@EventListener
	public void donate(NwmDonateEvent event) {
		try {
			EPNvWMDonate ep = event.getEP();
			Integer satisfaction = ep.getSatisfaction();
			statisticService.donate(ep.getGuId(), DateUtil.getTodayInt(), satisfaction);
			NvwmStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
