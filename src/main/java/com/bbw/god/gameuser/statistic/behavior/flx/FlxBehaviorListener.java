package com.bbw.god.gameuser.statistic.behavior.flx;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.server.flx.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 福临轩统计监听类
 * @date 2020/4/23 10:43
 */
@Component
@Slf4j
@Async
public class FlxBehaviorListener {
	@Autowired
	private FlxStatisticService flxStatisticService;

	@Order(2)
	@EventListener
	public void caishuziBet(CaiShuZiBetEvent event) {
		try {
			EPCaiShuZiBet ep = event.getEP();
			flxStatisticService.caishuziBet(ep.getGuId(), DateUtil.getTodayInt());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void yayaleBet(YaYaLeBetEvent event) {
		try {
			EPYaYaLeBet ep = event.getEP();
			flxStatisticService.yayaleBet(ep.getGuId(), DateUtil.getTodayInt());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void caishuziWin(CaiShuZiWinEvent event) {
		try {
			EPCaiShuZiWin ep = event.getEP();
			flxStatisticService.caishuziWin(ep.getGuId());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void yayaleWin(YaYaLeWinEvent event) {
		try {
			EPYaYaLeWin ep = event.getEP();
			flxStatisticService.yayaleWin(ep.getGuId());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void caishuziFail(CaiShuZiFailEvent event) {
		try {
			EPCaiShuZiFail ep = event.getEP();
			flxStatisticService.caishuziFail(ep.getGuId());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void yayaleFail(YaYaLeFailEvent event) {
		try {
			EPYaYaLeFail ep = event.getEP();
			flxStatisticService.yayaleFail(ep.getGuId());
			FlxStatistic statistic = flxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
