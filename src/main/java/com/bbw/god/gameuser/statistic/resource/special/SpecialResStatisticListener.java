package com.bbw.god.gameuser.statistic.resource.special;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.special.event.*;
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
 * @description 特产统计监听类
 * @date 2020/4/20 16:30
 */
@Component
@Slf4j
@Async
public class SpecialResStatisticListener {
	@Autowired
	private SpecialResStatisticService specialStatisticService;

	@Order(2)
	@EventListener
	public void addSpecial(SpecialAddEvent event) {
		try {
			EPSpecialAdd ep = event.getEP();
			List<EVSpecialAdd> addSpecials = ep.getAddSpecials();
			specialStatisticService.increment(ep.getGuId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt(),
					addSpecials.size(), ep.getWay());
			SpecialStatistic statistic = specialStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void deductSpecial(SpecialDeductEvent event) {
		try {
			EPSpecialDeduct ep = event.getEP();
			List<EPSpecialDeduct.SpecialInfo> infoList = ep.getSpecialInfoList();
			specialStatisticService.increment(ep.getGuId(), StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(),
					infoList.size(), ep.getWay());
			SpecialStatistic statistic = specialStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.CONSUME,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
