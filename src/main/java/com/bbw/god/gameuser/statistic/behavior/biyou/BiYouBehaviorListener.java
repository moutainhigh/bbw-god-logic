package com.bbw.god.gameuser.statistic.behavior.biyou;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouGainAward;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouGainAwardEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 碧游宫统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class BiYouBehaviorListener {
	@Autowired
	private BiYouStatisticService statisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void draw(EPBiyouGainAwardEvent event) {
		try {
			EPBiyouGainAward ep = event.getEP();
			Long uid = ep.getGuId();
			List<Award> awards = ep.getAwards();
			if (awards.isEmpty()) {
				return;
			}
			if (ep.isNewAward() && ep.getChapter() >= Chapter.SB1.getValue()) {
				statisticService.draw(uid, DateUtil.getTodayInt());
			}
			BiYouStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
