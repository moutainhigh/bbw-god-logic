//package com.bbw.god.gameuser.achievement.behavior.coc;
//
//import com.bbw.common.DateUtil;
//import com.bbw.god.gameuser.achievement.UserAchievementInfo;
//import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
//import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
//import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
//import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
//import com.bbw.god.gameuser.statistic.behavior.task.CocTaskStatistic;
//import org.springframework.stereotype.Service;
//
///**
// * @author suchaobin
// * @description 成就id=13370的service
// * @date 2020/5/18 9:59
// **/
//@Service
//public class AchievementService13370 extends BehaviorAchievementService {
//	/**
//	 * 获取当前行为类型
//	 *
//	 * @return 当前行为类型
//	 */
//	@Override
//	public BehaviorType getMyBehaviorType() {
//		return BehaviorType.CHAMBER_OF_COMMERCE_EXP_TASK;
//	}
//
//	/**
//	 * 获取当前成就id
//	 *
//	 * @return 当前成就id
//	 */
//	@Override
//	public int getMyAchievementId() {
//		return 13370;
//	}
//
//	/**
//	 * 获取当前成就进度
//	 *
//	 * @param uid  玩家id
//	 * @param info 成就对象信息
//	 * @return 当前成就进度
//	 */
//	@Override
//	public int getMyProgress(long uid, UserAchievementInfo info) {
//		if (isAccomplished(info)) {
//			return getMyNeedValue();
//		}
//		BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
//		CocTaskStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
//		return statistic.getTotal();
//	}
//}
