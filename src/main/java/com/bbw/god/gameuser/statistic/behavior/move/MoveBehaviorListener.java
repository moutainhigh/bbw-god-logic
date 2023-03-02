package com.bbw.god.gameuser.statistic.behavior.move;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.shake.EPShake;
import com.bbw.god.gameuser.shake.ShakeEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 移动统计监听类
 * @date 2020/4/22 15:55
 */
@Component
@Slf4j
@Async
public class MoveBehaviorListener {
	@Autowired
	private MoveStatisticService moveStatisticService;

	@Order(2)
	@EventListener
	public void move(ShakeEvent event) {
		try {
			EPShake ep = event.getEP();
			List<Integer> shakeList = ep.getShakeList();
			int sum = ListUtil.sumInt(shakeList);
			moveStatisticService.increment(ep.getGuId(), DateUtil.getTodayInt(), sum);
			MoveStatistic statistic = moveStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubMoveBehaviorEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
