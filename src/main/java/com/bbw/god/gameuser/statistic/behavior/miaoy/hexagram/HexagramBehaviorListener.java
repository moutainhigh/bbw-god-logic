package com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.miaoy.hexagram.HexagramAchievementEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramLevelEnum;
import com.bbw.god.city.miaoy.hexagram.event.EPHexagram;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEvent;
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
 * @description 文王64卦统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class HexagramBehaviorListener {
	@Autowired
	private HexagramStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(HexagramEvent event) {
		try {
			EPHexagram ep = event.getEP();
			Long uid = ep.getGuId();
			HexagramAchievementEnum hexagram = HexagramAchievementEnum.fromValue(ep.getHexagramId());
			if(hexagram == null){
				throw new ExceptionForClientTip("city.my.unvalid.hexagram");
			}
			boolean isUpUp = HexagramLevelEnum.UP_UP.getLevel() == hexagram.getHexagramLevel();
			boolean isDownDown = HexagramLevelEnum.DOWN_DOWN.getLevel() == hexagram.getHexagramLevel();
			statisticService.draw(uid, DateUtil.getTodayInt(), ep.isNewHexagram(),isUpUp,isDownDown);
			HexagramStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
