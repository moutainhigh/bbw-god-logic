package com.bbw.god.gameuser.statistic.behavior;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.GodException;
import com.bbw.god.gameuser.statistic.BaseStatisticService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;
import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 行为统计service
 * @date 2020/4/16 9:55
 */
@Service
public abstract class BehaviorStatisticService extends BaseStatisticService {
	@Autowired
	protected RedisHashUtil<String, Integer> redisHashUtil;

	/**
	 * 获取当前行为类型
	 *
	 * @return 当前行为类型
	 */
	public abstract BehaviorType getMyBehaviorType();

	/**
	 * 获取redis的key
	 *
	 * @param uid      玩家id
	 * @param typeEnum 类型枚举
	 * @return redis的key
	 */
	@Override
	public String getKey(long uid, StatisticTypeEnum typeEnum) {
		return "usr" + SPLIT + uid + SPLIT + "0statistic" + SPLIT + "behavior" + SPLIT +
				getMyBehaviorType().getValue();
	}

	/**
	 * 增加行为统计值
	 *
	 * @param uid      玩家id
	 * @param date     日期
	 * @param addValue 增加值
	 */
	public void increment(long uid, Integer date, int addValue) {
		if (addValue < 0) {
			throw new GodException("统计增加值为负数");
		}
		String key = getKey(uid, StatisticTypeEnum.NONE);
		redisHashUtil.increment(key, date + UNDERLINE + NUM, addValue);
		redisHashUtil.increment(key, TOTAL, addValue);
		statisticPool.toUpdatePool(key);
	}

	/**
	 * 清理统计数据
	 *
	 * @param uid 玩家id
	 */
	@Override
	public void clean(long uid) {
		cleanByKey(getKey(uid, StatisticTypeEnum.NONE));
	}
}
