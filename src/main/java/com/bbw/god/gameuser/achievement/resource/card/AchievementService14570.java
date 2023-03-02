package com.bbw.god.gameuser.achievement.resource.card;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.card.CardStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author suchaobin
 * @description 成就id=14570的service
 * @date 2020/5/27 8:54
 **/
@Service
public class AchievementService14570 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.KP;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 14570;
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
		CardStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		Map<WayEnum, Integer> fiveStarWayMap = statistic.getFiveStarWayMap();
		return fiveStarWayMap.get(WayEnum.YG_ELITE_OPEN_BOX) == null ? 0 :
				fiveStarWayMap.get(WayEnum.YG_ELITE_OPEN_BOX);
	}
}
