package com.bbw.god.gameuser.statistic.resource.dice;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.res.dice.DiceAddEvent;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceAdd;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
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
 * @description 体力统计监听类
 * @date 2020/4/20 16:30
 */
@Component
@Slf4j
@Async
public class DiceResStatisticListener {
	@Autowired
	private DiceResStatisticService statisticService;

	@Order(2)
	@EventListener
	public void addDice(DiceAddEvent event) {
		try {
			EPDiceAdd ep = event.getEP();
			int addDice = ep.getAddDice();
			Long uid = ep.getGuId();
			statisticService.increment(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt(), addDice);
			DiceStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
			StatisticEventPublisher.pubDiceResourceEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void deductDice(DiceDeductEvent event) {
		try {
			EPDiceDeduct ep = event.getEP();
			int deductDice = ep.getDeductDice();
			Long uid = ep.getGuId();
			statisticService.increment(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(), deductDice);
			DiceStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
			StatisticEventPublisher.pubDiceResourceEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
