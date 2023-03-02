package com.bbw.god.gameuser.achievement.behavior.fight;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.fight.fightdetail.FightDetailStatistic;
import org.springframework.stereotype.Service;

/**哮天神犬
 * @author lzc
 * @date 2021/4/13 10:01
 **/
@Service
public class AchievementService15030 extends BehaviorAchievementService {

	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	@Override
	public BehaviorType getMyBehaviorType() {
		return BehaviorType.FIGHT_DETAIL;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 15030;
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
		FightDetailStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
		int progress = 0;
		if(statistic.getNightmareMainCity4325LeaderKillNum() >= 5){
			progress += 1;
		}
		if(statistic.getNightmareMainCity2539LeaderKillNum() >= 5){
			progress += 1;
		}
		if(statistic.getNightmareMainCity1024LeaderKillNum() >= 5){
			progress += 1;
		}
		if(statistic.getNightmareMainCity2608LeaderKillNum() >= 5){
			progress += 1;
		}
		if(statistic.getNightmareMainCity2725LeaderKillNum() >= 5){
			progress += 1;
		}
		return progress;
	}
}
