package com.bbw.god.gameuser.statistic.resource.friend;

import com.bbw.common.DateUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.buddy.event.BuddyAcceptEvent;
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
 * @description 好友统计监听类
 * @date 2020/4/16 14:16
 */
@Component
@Slf4j
@Async
public class FriendResListener {
	@Autowired
	private FriendResStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void addFriend(BuddyAcceptEvent event) {
		try {
			EventParam<Long> ep = (EventParam<Long>) event.getSource();
			Long uid = ep.getGuId();
			Long friendId = ep.getValue();
			statisticService.addFriend(uid, DateUtil.getTodayInt());
			statisticService.addFriend(friendId, DateUtil.getTodayInt());
			FriendStatistic myStatistic = statisticService.fromRedis(uid, StatisticTypeEnum.GAIN,
					DateUtil.getTodayInt());
			FriendStatistic friendStatistic = statisticService.fromRedis(friendId, StatisticTypeEnum.GAIN,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(uid, ep.getWay(), ep.getRd(), myStatistic);
			StatisticEventPublisher.pubResourceStatisticEvent(friendId, ep.getWay(), ep.getRd(), friendStatistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
