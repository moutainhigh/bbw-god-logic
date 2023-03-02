package com.bbw.god.gameuser.achievement.behavior.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.sxdh.SxdhStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author suchaobin
 * @description 成就id=14500的service
 * @date 2020/5/19 15:15
 **/
@Service
public class AchievementService14500 extends BehaviorAchievementService {
	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.SXDH_FIGHT;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 14500;
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
		SxdhStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		Map<String, Integer> rankMap = statistic.getMiddleSeasonRankMap();
		Set<String> keySet = rankMap.keySet();
		int value = 0;
		for (String key : keySet) {
			if (rankMap.get(key) <= 10) {
				value++;
			}
		}
		return value;
	}
}
