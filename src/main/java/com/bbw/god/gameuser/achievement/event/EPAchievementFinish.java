package com.bbw.god.gameuser.achievement.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 成就已领取事件(主要用于广播)
 * @date 2020/2/28 10:53
 */
@Data
public class EPAchievementFinish extends BaseEventParam {
	private Integer achievementId;

	public EPAchievementFinish(Integer achievementId, BaseEventParam bep) {
		this.achievementId = achievementId;
		setValues(bep);
	}
}
