package com.bbw.god.statistics.userstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.redis.ServerRedisKey;

/**
 * 玩家行为统计（临时）
 * 
 * @author suhq
 * @date 2019-08-02 11:12:14
 */
@Service
public class UserActionStatisticService {
	private static int DATE_STATISTIC_EXPIRE_TIME = 2 * 24 * 60 * 60;// 过期时间，单位秒
	@Autowired
	private GameUserService gameUserService;

	@Autowired
	private RedisHashUtil<String, Integer> redisHashUtil;

	/**
	 * 增加玩家行为统计值
	 *
	 * @param
	 * @param
	 * @param addNum
	 */
	public void add(Long uid, int addNum, String way) {
		redisHashUtil.increment(getUserActionStatisticKey(uid), way, addNum);
		redisHashUtil.expire(getUserActionStatisticKey(uid), DATE_STATISTIC_EXPIRE_TIME);
	}

	/**
	 * 获取值
	 * 
	 * @param uid
	 * @param way
	 * @return
	 */
	public int get(long uid, String way) {
		Integer num = redisHashUtil.getField(getUserActionStatisticKey(uid), way);
		return num == null ? 0 : num;
	}

	/**
	 * 玩家统计的基础key
	 *
	 * @param uid
	 * @return
	 */
	private String getUserActionStatisticKey(Long uid) {
		int sid = gameUserService.getActiveSid(uid);
		return ServerRedisKey.PREFIX + ServerRedisKey.SPLIT + sid + ServerRedisKey.SPLIT + "statistic" + ServerRedisKey.SPLIT + DateUtil.getTodayInt() + ServerRedisKey.SPLIT + "0usr" + ServerRedisKey.SPLIT + uid + ServerRedisKey.SPLIT + "action";
	}
}
