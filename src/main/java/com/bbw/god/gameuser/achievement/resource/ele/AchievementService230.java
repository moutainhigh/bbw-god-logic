package com.bbw.god.gameuser.achievement.resource.ele;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.ele.EleStatistic;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author suchaobin
 * @description 成就230的service
 * @date 2020/5/14 17:32
 **/
@Service
public class AchievementService230 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.YS;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 230;
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
		EleStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt());
		Map<WayEnum, Integer> woodMap = statistic.getWoodMap();
		return woodMap.get(WayEnum.CARD_UPDATE) == null ? 0 : woodMap.get(WayEnum.CARD_UPDATE);
	}
}
