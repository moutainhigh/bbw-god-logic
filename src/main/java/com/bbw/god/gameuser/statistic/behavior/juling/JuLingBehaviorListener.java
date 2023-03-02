package com.bbw.god.gameuser.statistic.behavior.juling;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 聚灵统计监听类
 * @date 2020/4/23 12:09
 */
@Component
@Slf4j
@Async
public class JuLingBehaviorListener {
	@Autowired
	private JuLingStatisticService statisticService;

	@Order(2)
	@EventListener
	public void juLing(UserCardAddEvent event) {
		try {
			EPCardAdd ep = event.getEP();
			WayEnum way = ep.getWay();
			Long guId = ep.getGuId();
			RDCommon rd = ep.getRd();
			// 聚灵且随机获得的
			if (way == WayEnum.CARD_JL && ep.isRandom()) {
				statisticService.juLing(guId, DateUtil.getTodayInt(), ep.getAddCards().get(0).getCardId());
				JuLingStatistic statistic = statisticService.fromRedis(guId, StatisticTypeEnum.NONE,
						DateUtil.getTodayInt());
				StatisticEventPublisher.pubBehaviorStatisticEvent(guId, ep.getWay(), rd, statistic);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
