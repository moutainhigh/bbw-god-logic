package com.bbw.god.gameuser.achievement.resource.copper;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 成就id=14280的service
 * @date 2020/5/18 10:21
 **/
@Service
public class AchievementService14280 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.TQ;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 14280;
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
		CopperStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		return (int) (statistic.getTotalProfit() / 10000);
	}

	/**
	 * 获取当前成就进度(用于判断成就是否完成)
	 *
	 * @param uid  玩家id
	 * @param info 成就对象信息
	 * @return 当前成就进度
	 */
	@Override
	public int getMyValueForAchieve(long uid, UserAchievementInfo info) {
		if (isAccomplished(info)) {
			return getMyNeedValue();
		}
		ResourceStatisticService service = statisticServiceFactory.getByAwardEnum(getMyAwardEnum());
		CopperStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		return statistic.getTotalProfit().intValue();
	}
}
