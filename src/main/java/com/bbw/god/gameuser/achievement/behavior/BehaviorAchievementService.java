package com.bbw.god.gameuser.achievement.behavior;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 行为成就基础service
 * @date 2020/5/14 11:12
 **/
@Service
public abstract class BehaviorAchievementService extends BaseAchievementService {
	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	public abstract BehaviorType getMyBehaviorType();
}
