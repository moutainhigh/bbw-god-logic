package com.bbw.god.gameuser.statistic.behavior.box;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.random.box.EPOpenBox;
import com.bbw.god.random.box.OpenBoxEvent;
import com.bbw.god.server.guild.event.EPGuildOpenBox;
import com.bbw.god.server.guild.event.GuildOpenBoxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 宝箱行为监听
 * @date 2020/4/21 14:47
 */
@Component
@Slf4j
@Async
public class BoxBehaviorListener {
	@Autowired
	private OpenDailyTaskBoxStatisticService dailyTaskBoxStatisticService;
	@Autowired
	private OpenGuildBoxStatisticService guildBoxStatisticService;

	@Order(2)
	@EventListener
	public void openDailyTaskBox(OpenBoxEvent event) {
		try {
			EPOpenBox ep = event.getEP();
			dailyTaskBoxStatisticService.openBox(ep.getGuId(), DateUtil.getTodayInt(), ep.getBoxId(), ep.getScore());
			OpenDailyTaskBoxStatistic statistic = dailyTaskBoxStatisticService.fromRedis(ep.getGuId(),
					StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void openGuildBox(GuildOpenBoxEvent event) {
		try {
			EPGuildOpenBox ep = event.getEP();
			guildBoxStatisticService.increment(ep.getGuId(), DateUtil.getTodayInt(), 1);
			OpenGuildBoxStatistic statistic = guildBoxStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
