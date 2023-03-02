package com.bbw.god.gameuser.statistic.behavior.zhibao;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.card.equipment.event.EPCardZhiBaoAdd;
import com.bbw.god.gameuser.card.equipment.event.ZhiBaoEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 至宝新增行为监听
 *
 * @author: huanghb
 * @date: 2022/9/27 10:09
 */
@Component
@Slf4j
@Async
public class ZhiBaoBehaviorListener {
	@Autowired
	private ZhiBaoStatistucService statisticService;

	@Order(2)
	@EventListener
	public void addZhiBao(ZhiBaoEvent event) {
		try {
			EPCardZhiBaoAdd ep = event.getEP();
			long uid = ep.getGuId();
			statisticService.addZhiBaoStatistic(uid, ep);
			ZhiBaoStatistic zhiBaoStatistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), zhiBaoStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
