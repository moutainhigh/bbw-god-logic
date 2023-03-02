package com.bbw.god.gameuser.achievement.resource;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.statistic.event.EPResourceStatistic;
import com.bbw.god.gameuser.statistic.event.ResourceStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.gameuser.statistic.resource.copper.CopperResourceStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.ele.EleResourceStatisticEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 资源监听
 *
 * @author: suhq
 * @date: 2021/8/18 5:29 下午
 */
@Async
@Component
@Slf4j
public class ResAchievementListener {
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserCardService userCardService;

	@Order(1000)
	@EventListener
	public void achievement(ResourceStatisticEvent event) {
		doAchieve(event.getEP());
	}

	@EventListener
	@Order(1000)
	public void copperAchievement(CopperResourceStatisticEvent event) {
		doAchieve(event.getEP());
	}

	@Order(1000)
	@EventListener
	public void eleAchievement(EleResourceStatisticEvent event) {
		doAchieve(event.getEP());
	}

	private void doAchieve(EPResourceStatistic ep) {
		try {
			List<AwardEnum> excludes = Arrays.asList(AwardEnum.YB);

			Long uid = ep.getGuId();
			ResourceStatistic statistic = ep.getResourceStatistic();
			if (excludes.contains(statistic.getAwardEnum())) {
				return;
			}
			List<ResourceAchievementService> services = achievementServiceFactory.getByAwardEnum(statistic.getAwardEnum());
			UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
			for (ResourceAchievementService service : services) {
				if (service.isAccomplished(info)) {
					continue;
				}
				int value = service.getMyValueForAchieve(uid, info);
				service.achieve(uid, value, info, ep.getRd());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
