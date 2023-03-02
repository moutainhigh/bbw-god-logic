package com.bbw.god.gameuser.achievement.behavior.biyou;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.biyou.BiYouStatistic;
import org.springframework.stereotype.Service;

/**修炼有成
 * @author lzc
 * @date 2021/4/13 10:01
 **/
@Service
public class AchievementService14980 extends BehaviorAchievementService {

	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.BI_YOU;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 14980;
	}

	/**
	 * 获取当前成就进度(用于展示给客户端)
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
		BiYouStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		return statistic.getMzSkillNum();
	}
}
