package com.bbw.god.gameuser.statistic.resource.treasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.treasure.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 法宝统计监听类
 * @date 2020/4/20 9:40
 */
@Component
@Slf4j
@Async
public class TreasureResListener {
	@Autowired
	private TreasureResStatisticService statisticService;

	@Order(2)
	@EventListener
	public void addTreasure(TreasureAddEvent event) {
		try {
			EPTreasureAdd ep = event.getEP();
			List<EVTreasure> addTreasures = ep.getAddTreasures();
			Long uid = ep.getGuId();
			statisticService.increment(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt(), addTreasures, ep.getWay());
			TreasureStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.GAIN,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void deductTreasure(TreasureDeductEvent event) {
		try {
			EPTreasureDeduct ep = event.getEP();
			EVTreasure deductTreasure = ep.getDeductTreasure();
			Long uid = ep.getGuId();
			Integer treasureId = deductTreasure.getId();
			Integer deductNum = deductTreasure.getNum();
			CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
			statisticService.increment(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(), treasure, ep.getWay(),
					deductNum);
			TreasureStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.CONSUME,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
