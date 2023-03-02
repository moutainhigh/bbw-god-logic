package com.bbw.god.gameuser.statistic.resource.ele;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.res.ele.*;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 元素统计监听类
 * @date 2020/4/20 11:25
 */
@Component
@Slf4j
@Async
public class EleResListener {
	@Autowired
	private EleResStatisticService eleStatisticService;

	@Order(2)
	@EventListener
	public void addEle(EleAddEvent event) {
		try {
			EPEleAdd ep = event.getEP();
			Long uid = ep.getGuId();
			WayEnum way = ep.getWay();
			List<EVEle> addEles = ep.getAddEles();
			increment(uid, StatisticTypeEnum.GAIN, way, addEles);
			EleStatistic statistic = eleStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
			StatisticEventPublisher.pubEleResourceEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void deductEle(EleDeductEvent event) {
		try {
			EPEleDeduct ep = event.getEP();
			Long uid = ep.getGuId();
			List<EVEle> deductEles = ep.getDeductEles();
			increment(uid, StatisticTypeEnum.CONSUME, ep.getWay(), deductEles);
			EleStatistic statistic = eleStatisticService.fromRedis(uid, StatisticTypeEnum.CONSUME,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubEleResourceEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void increment(Long uid, StatisticTypeEnum typeEnum, WayEnum way, List<EVEle> addEles) {
		Map<Integer, Integer> eleGroup = addEles.stream().collect(Collectors.groupingBy(EVEle::getType, Collectors.summingInt(EVEle::getNum)));
		int gold = eleGroup.getOrDefault(10, 0);
		int wood = eleGroup.getOrDefault(20, 0);
		int water = eleGroup.getOrDefault(30, 0);
		int fire = eleGroup.getOrDefault(40, 0);
		int earth = eleGroup.getOrDefault(50, 0);
		eleStatisticService.increment(uid, typeEnum, DateUtil.getTodayInt(), gold, wood, water, fire, earth, way);
	}
}
