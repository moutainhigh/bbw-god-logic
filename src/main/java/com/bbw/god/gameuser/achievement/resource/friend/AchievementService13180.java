package com.bbw.god.gameuser.achievement.resource.friend;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.friend.FriendStatistic;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 成就id=13180的service
 * @date 2020/5/22 10:20
 **/
@Service
public class AchievementService13180 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.FRIEND;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13180;
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
		FriendStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		return statistic.getTotal();
	}
}