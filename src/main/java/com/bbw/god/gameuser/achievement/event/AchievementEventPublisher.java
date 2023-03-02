package com.bbw.god.gameuser.achievement.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 成就事件发布器(目前仅针对成就的状态)
 * @date 2020/2/28 10:58
 */
public class AchievementEventPublisher {
	public static void pubAchievementFinishEvent(int achievementId, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new AchievementFinishEvent(new EPAchievementFinish(achievementId, bep)));
	}
}
