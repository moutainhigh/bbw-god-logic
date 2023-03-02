package com.bbw.god.gameuser.statistic.behavior.login;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.EPFirstLoginPerDay;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 登录统计监听类
 * @date 2020/4/21 15:13
 */
@Component
@Slf4j
@Async
public class LoginBehaviorListener {
	@Autowired
	private LoginStatisticService loginStatisticService;

	@Order(2)
	@EventListener
	public void login(LoginEvent event) {
		try {
			LoginPlayer loginPlayer = event.getLoginPlayer();
			Long uid = loginPlayer.getUid();
			loginStatisticService.incLoginTimes(uid);
			LoginStatistic loginStatistic = loginStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubLoginBehaviorEvent(uid, WayEnum.NONE, new RDCommon(), loginStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void loginPerDay(FirstLoginPerDayEvent event) {
		try {
			EPFirstLoginPerDay ep = event.getEP();
			Long uid = ep.getGuId();
			loginStatisticService.incLoginDays(uid);
			LoginStatistic loginStatistic = loginStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubLoginBehaviorEvent(uid, ep.getWay(), ep.getRd(), loginStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
