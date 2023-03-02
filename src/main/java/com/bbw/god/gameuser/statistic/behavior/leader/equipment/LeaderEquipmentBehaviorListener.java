package com.bbw.god.gameuser.statistic.behavior.leader.equipment;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.leadercard.event.EPLeaderEquipmentAddLv;
import com.bbw.god.gameuser.leadercard.event.EPLeaderEquipmentQualityFinish;
import com.bbw.god.gameuser.leadercard.event.LeaderEquipmentAddLvEvent;
import com.bbw.god.gameuser.leadercard.event.LeaderEquipmentQualityFinishEvent;
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
 * @description 法外分身装备统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class LeaderEquipmentBehaviorListener {
	@Autowired
	private LeaderEquipmentStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(LeaderEquipmentQualityFinishEvent event) {
		try {
			EPLeaderEquipmentQualityFinish ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid,ep.getEquipmentId(),ep.getQuality(),true);
			LeaderEquipmentStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void equipmentAddLv(LeaderEquipmentAddLvEvent event) {
		try {
			EPLeaderEquipmentAddLv ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid,ep.getEquipmentId(),ep.getLevel(),false);
			LeaderEquipmentStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
