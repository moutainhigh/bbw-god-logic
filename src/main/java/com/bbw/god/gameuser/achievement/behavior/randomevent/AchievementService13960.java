package com.bbw.god.gameuser.achievement.behavior.randomevent;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.randomevent.RandomEventStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author suchaobin
 * @description 成就id=13960的service
 * @date 2020/5/27 8:54
 **/
@Service
public class AchievementService13960 extends BehaviorAchievementService {
	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.RANDOM_EVENT;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13960;
	}

	/**
	 * 获取当前成就进度
	 *
	 * @param uid  玩家id
	 * @param info 成就对象信息
	 * @return 当前成就进度
	 */
	@Override
	public int getMyProgress(long uid, UserAchievementInfo info) {
		if (isAccomplished(info)) {
			return getMyNeedValue();
		}
		BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
		RandomEventStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		Map<YdEventEnum, Integer> todayMap = statistic.getTodayMap();
		return todayMap.get(YdEventEnum.LAO_NAI_NAI) == null ? 0 : todayMap.get(YdEventEnum.LAO_NAI_NAI);
	}
}
