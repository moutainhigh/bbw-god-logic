package com.bbw.god.gameuser.achievement.behavior.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.sxdh.SxdhStatistic;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 成就id=14480的service
 * @date 2020/5/19 15:15
 **/
@Service
public class AchievementService14480 extends BehaviorAchievementService {
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
		return 14480;
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
		Map<String, Integer> rankMap = statistic.getSeasonRankMap();
		Set<String> keySet = rankMap.keySet();
		List<String> keys = new ArrayList<>();
		for (String key : keySet) {
			if (rankMap.get(key) <= 3) {
				keys.add(key);
			}
		}
		List<Date> dateList = keys.stream().map(k -> DateUtil.fromDateInt(Integer.parseInt(k + "01")))
				.sorted(Date::compareTo).collect(Collectors.toList());
		if (ListUtil.isEmpty(dateList)) {
			return 0;
		}
		int value = 1;
		for (int i = 0; i < dateList.size() - 1; i++) {
			int monthsBetween = DateUtil.getMonthsBetween(dateList.get(i), dateList.get(i + 1));
			if (1 == monthsBetween || -1 == monthsBetween) {
				value++;
			} else {
				value = 1;
			}
			if (value >= 3) {
				break;
			}
		}
		return value;
	}
}
