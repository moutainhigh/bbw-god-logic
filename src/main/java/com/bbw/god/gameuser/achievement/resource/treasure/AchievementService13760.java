package com.bbw.god.gameuser.achievement.resource.treasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author suchaobin
 * @description 成就id=13760的service
 * @date 2020/5/25 15:04
 **/
@Service
public class AchievementService13760 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.FB;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13760;
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
		ResourceStatisticService service = statisticServiceFactory.getByAwardEnum(getMyAwardEnum());
		TreasureStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		return getProgress(statistic);
	}

	protected int getProgress(TreasureStatistic statistic) {
		Map<String, Map<WayEnum, Integer>> totalMap = statistic.getTotalMap();
		int sum = 0;
		Set<String> totalKeySet = totalMap.keySet();
		for (String key : totalKeySet) {
			Map<WayEnum, Integer> wayMap = totalMap.get(key);
			sum += wayMap.get(WayEnum.XRD) == null ? 0 : wayMap.get(WayEnum.XRD);
		}
		return sum;
	}
}
