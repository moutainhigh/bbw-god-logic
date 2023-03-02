package com.bbw.god.gameuser.achievement.resource.treasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 成就id=13230的service
 * @date 2020/5/22 10:24
 **/
@Service
public class AchievementService13230 extends ResourceAchievementService {
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
		return 13230;
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
		Map<String, Map<WayEnum, Integer>> totalMap = statistic.getTotalMap();
		List<String> list = TreasureTool.getAllTreasures().stream().filter(t -> t.getId() >= 21001 &&
				t.getId() <= 21185).map(CfgTreasureEntity::getName).collect(Collectors.toList());
		int total = 0;
		for (String key : list) {
			Map<WayEnum, Integer> wayEnumMap = totalMap.get(key);
			int sum = 0;
			if (null != wayEnumMap) {
				Set<WayEnum> keySet = wayEnumMap.keySet();
				for (WayEnum way : keySet) {
					sum += wayEnumMap.get(way);
				}
			}
			total += sum;
		}
		return total;
	}
}
