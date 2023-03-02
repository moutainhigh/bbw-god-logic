package com.bbw.god.gameuser.statistic.behavior.maou;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouKilledEvent;
import com.bbw.god.server.maou.alonemaou.event.EPAloneMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 独战魔王统计监听类
 * @date 2020/4/22 10:52
 */
@Component
@Slf4j
@Async
public class AloneMaouListener {
	@Autowired
	private AloneMaouStatisticService aloneMaouStatisticService;

	@Order(2)
	@EventListener
	public void killAloneMaou(AloneMaouKilledEvent event) {
		try {
			EPAloneMaou ep = event.getEP();
			Long uid = ep.getGuId();
			ServerAloneMaou aloneMaou = ep.getAloneMaou();
			Integer type = aloneMaou.getType();
			AloneMaouLevelInfo maouLevelInfo = ep.getMaouLevelInfo();
			Integer maouLevel = maouLevelInfo.getMaouLevel();
			aloneMaouStatisticService.killAloneMaou(uid, maouLevel, type);
			AloneMaouStatistic statistic = aloneMaouStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
