package com.bbw.god.gameuser.statistic.behavior.leader;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.leadercard.event.LeaderCardActiveSkillTreeEvent;
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
 * @description 技能树统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class LeaderCardSkillTreeBehaviorListener {
	@Autowired
	private LeaderCardSkillTreeStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(LeaderCardActiveSkillTreeEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid, DateUtil.getTodayInt());
			LeaderCardSkillTreeStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
