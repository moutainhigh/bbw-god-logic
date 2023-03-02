package com.bbw.god.gameuser.achievement.resource.treasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 成就id=13280的service
 * @date 2020/5/22 10:44
 **/
@Service
public class AchievementService13280 extends ResourceAchievementService {
	private static final List<Integer> defendSymbolList = Arrays.asList(20210, 20220, 20230, 20240, 20250, 20260);


	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.FB;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13280;
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
		TreasureStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
		return getProgress(statistic);
	}

	protected int getProgress(TreasureStatistic statistic) {
		Map<String, Map<WayEnum, Integer>> totalMap = statistic.getTotalMap();
		Set<String> keys = defendSymbolList.stream().map(s ->
				TreasureTool.getTreasureById(s).getName()).collect(Collectors.toSet());
		int sum = 0;
		for (String key : keys) {
			Map<WayEnum, Integer> wayEnumMap = totalMap.get(key);
			if (null != wayEnumMap) {
				sum++;
			}
		}
		return sum;
	}
}
