package com.bbw.god.gameuser.achievement.resource.special;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.special.SpecialStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author suchaobin
 * @description 成就id=13500的service
 * @date 2020/5/18 10:16
 **/
@Service
public class AchievementService13500 extends ResourceAchievementService {

	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.TC;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13500;
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
		SpecialStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		Map<WayEnum, Integer> totalMap = statistic.getTotalMap();
		int sum = 0;
		Set<WayEnum> keySet = totalMap.keySet();
		for (WayEnum way : keySet) {
			if (WayEnum.TRADE != way) {
				sum += totalMap.get(way);
			}
		}
		return sum;
	}
}
