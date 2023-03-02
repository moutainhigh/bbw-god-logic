package com.bbw.god.gameuser.achievement.behavior.leader;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.leader.equipment.LeaderEquipmentStatistic;
import org.springframework.stereotype.Service;

/**曼珠沙华
 * @author lzc
 * @date 2021/4/13 10:01
 **/
@Service
public class AchievementService15140 extends BehaviorAchievementService {

	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.LEADER_EQUIPMENT;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 15140;
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
		LeaderEquipmentStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		//（0未获得、凡品10、中20、上品30、精品40、极品50、仙品60）
		if(statistic.getRingQuality() >= 60 && statistic.getNecklaceQuality() >= 60){
			return 1;
		}
		return 0;
	}
}
