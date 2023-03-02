package com.bbw.god.gameuser.achievement.behavior.maou;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.maou.AloneMaouStatistic;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * @author suchaobin
 * @description 成就id=830的service
 * @date 2020/5/25 9:24
 **/
@Service
public class AchievementService830 extends BehaviorAchievementService {
	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.MAOU_ALONE_FIGHT;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 830;
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
		AloneMaouStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		Integer gold = statistic.getGold();
		Integer wood = statistic.getWood();
		Integer water = statistic.getWater();
		Integer fire = statistic.getFire();
		Integer earth = statistic.getEarth();
		return (int) Stream.of(gold, wood, water, fire, earth).filter(s -> s >= 7).count();
	}
}
