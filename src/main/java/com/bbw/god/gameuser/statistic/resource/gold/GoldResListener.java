package com.bbw.god.gameuser.statistic.resource.gold;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
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
 * @description 元宝资源监听类
 * @date 2020/3/29 22:02
 */
@Component
@Slf4j
@Async
public class GoldResListener {
	@Autowired
	private GoldResStatisticService goldStatisticService;

	@Order(2)
	@EventListener
	public void addGold(GoldAddEvent event) {
		try {
			EPGoldAdd ep = event.getEP();
			int sum = ep.gainAddGold();
			Long uid = ep.getGuId();
			goldStatisticService.increment(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt(), sum, ep.getWay());
			GoldStatistic goldStatistic = goldStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(uid, ep.getWay(), ep.getRd(), goldStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void deductGold(GoldDeductEvent event) {
		try {
			EPGoldDeduct ep = event.getEP();
			int deductGold = ep.getDeductGold();
			Long uid = ep.getGuId();
			goldStatisticService.increment(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(), deductGold,
					ep.getWay());
			GoldStatistic goldStatistic = goldStatisticService.fromRedis(uid, StatisticTypeEnum.CONSUME,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(uid, ep.getWay(), ep.getRd(), goldStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
