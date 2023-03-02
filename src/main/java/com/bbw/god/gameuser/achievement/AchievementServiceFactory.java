package com.bbw.god.gameuser.achievement;

import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就工厂类
 * @date 2020/5/14 10:51
 **/
@Service
public class AchievementServiceFactory {
	@Autowired
	@Lazy
	private List<BaseAchievementService> baseAchievementServices;
	@Autowired
	@Lazy
	private List<ResourceAchievementService> resourceAchievementServices;
	@Autowired
	@Lazy
	private List<BehaviorAchievementService> BehaviorAchievementServices;

	/**
	 * 通过成就id获取对应service
	 *
	 * @param achievementId 成就id
	 * @return 成就id的对应service
	 */
	public BaseAchievementService getById(int achievementId) {
		for (BaseAchievementService service : baseAchievementServices) {
			if (achievementId == service.getMyAchievementId()) {
				return service;
			}
		}
		throw new CoderException(String.format("程序员没有编写成就id=%s的service", achievementId));
	}

	/**
	 * 通过资源类型获取对应service集合
	 *
	 * @param awardEnum 资源类型
	 * @return 资源类型对应的service集合
	 */
	public List<ResourceAchievementService> getByAwardEnum(AwardEnum awardEnum) {
		List<ResourceAchievementService> list = new ArrayList<>();
		for (ResourceAchievementService service : resourceAchievementServices) {
			if (service.getMyAwardEnum() == awardEnum) {
				list.add(service);
			}
		}
		if (ListUtil.isEmpty(list)) {
			throw new CoderException(String.format("程序员没有编写资源类型=%s的service", awardEnum.getName()));
		}
		return list;
	}

	/**
	 * 通过行为类型获取对应service集合
	 *
	 * @param behaviorType 行为类型
	 * @return 行为类型对应的service集合
	 */
	public List<BehaviorAchievementService> getByBehaviorType(BehaviorType behaviorType) {
		List<BehaviorAchievementService> list = new ArrayList<>();
		for (BehaviorAchievementService service : BehaviorAchievementServices) {
			if (service.getMyBehaviorType() == behaviorType) {
				list.add(service);
			}
		}
		if (ListUtil.isEmpty(list)) {
			throw new CoderException(String.format("程序员没有编写行为类型=%s的BehaviorAchievementService", behaviorType.getName()));
		}
		return list;
	}
}
