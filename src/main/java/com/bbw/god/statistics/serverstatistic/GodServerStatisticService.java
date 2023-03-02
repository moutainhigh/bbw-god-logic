package com.bbw.god.statistics.serverstatistic;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.server.redis.ServerRedisKey;
import com.bbw.god.statistics.ServerStatistic;
import com.bbw.god.statistics.StatisticKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * @author suchaobin
 * @title: GodServerStatisticService
 * @projectName bbw-god-logic-server
 * @description: 区服数据统计逻辑层
 * @date 2019/6/1814:52
 */
@Service("GodServerStatisticService")
public class GodServerStatisticService {
	private static int DATE_STATISTIC_EXPIRE_TIME = 3 * 24 * 60 * 60;// 过期时间，单位秒
	@Autowired
	private RedisHashUtil<String, Integer> dateStatisticRedis;
	@Autowired
	private RedisHashUtil<String, ServerStatistic> statisticRedis;
	@Autowired
	private RedisZSetUtil<Long> redisZSetUtil;

	public void addStatistic(ServerStatistic ss) {
		statisticRedis.putField(getStatisticKey(ss.getSid()), ss.getKey(), ss);
	}

	public ServerStatistic getStatistic(int sid, StatisticKeyEnum se) {
		return statisticRedis.getField(getStatisticKey(sid), se.getKey());
	}

	/**
	 * 增加区服产出统计值
	 *
	 * @param sid
	 * @param
	 * @param addNum
	 */
	public void addOutput(int sid, WayEnum wayEnum, long addNum, String awardName) {
		String wayName = wayEnum.getName();
		dateStatisticRedis.increment(getDateStatisticOutputKey(sid, awardName), wayName, addNum);
		dateStatisticRedis.expire(getDateStatisticOutputKey(sid, awardName), DATE_STATISTIC_EXPIRE_TIME);
	}

	/**
	 * 增加区服消耗统计值
	 *
	 * @param sid
	 * @param
	 * @param addNum
	 */
	public void addConsume(int sid, WayEnum wayEnum, long addNum, String awardName) {
		String wayName = wayEnum.getName();
		dateStatisticRedis.increment(getDateStatisticConsumeKey(sid, awardName), wayName, addNum);
		dateStatisticRedis.expire(getDateStatisticConsumeKey(sid, awardName), DATE_STATISTIC_EXPIRE_TIME);
	}

	/**
	 * 区服产出统计的key
	 *
	 * @param sid
	 * @return
	 */
	public String getDateStatisticOutputKey(int sid, String awardName) {
		return getBaseDateStatisticKey(sid) + ServerRedisKey.SPLIT + awardName + ServerRedisKey.SPLIT + "output";
	}

	public String getDateStatisticOutputKey(int sid, String awardName, Date date) {
		return getBaseDateStatisticKey(sid, date) + ServerRedisKey.SPLIT + awardName + ServerRedisKey.SPLIT + "output";
	}

	/**
	 * 区服消耗统计的key
	 *
	 * @param sid
	 * @return
	 */
	public String getDateStatisticConsumeKey(int sid, String awardName) {
		return getBaseDateStatisticKey(sid) + ServerRedisKey.SPLIT + awardName + ServerRedisKey.SPLIT + "consume";
	}

	public String getDateStatisticConsumeKey(int sid, String awardName, Date date) {
		return getBaseDateStatisticKey(sid, date) + ServerRedisKey.SPLIT + awardName + ServerRedisKey.SPLIT +
				"consume";
	}

	/**
	 * 区服登录统计的key
	 *
	 * @param sid
	 * @return
	 */
	private String getLoginStatisticKey(int sid, Date date) {
		return getBaseStatisticKey(sid) + ServerRedisKey.SPLIT + DateUtil.toDateInt(date) + ServerRedisKey.SPLIT +
				"login";
	}

	/**
	 * 区服每日统计的基础key
	 *
	 * @param sid
	 * @return
	 */
	private String getBaseDateStatisticKey(int sid) {
		return getBaseStatisticKey(sid) + ServerRedisKey.SPLIT + DateUtil.getTodayInt();
	}

	private String getBaseDateStatisticKey(int sid, Date date) {
		return getBaseStatisticKey(sid) + ServerRedisKey.SPLIT + DateUtil.toDateInt(date);
	}

	/**
	 * 区服统计的基础key
	 *
	 * @param sid
	 * @return
	 */
	private String getStatisticKey(int sid) {
		return getBaseStatisticKey(sid) + ServerRedisKey.SPLIT + "0statistic";
	}

	/**
	 * 区服统计的基础key
	 *
	 * @param sid
	 * @return
	 */
	private String getBaseStatisticKey(int sid) {
		return ServerRedisKey.PREFIX + ServerRedisKey.SPLIT + sid + ServerRedisKey.SPLIT + "statistic";
	}

	private String getServerCocTaskKey(int sid) {
		return getBaseDateStatisticKey(sid) + ServerRedisKey.SPLIT + "商会任务";
	}

	public void addCocTask(int sid, EPTaskFinished evTaskFinished) {
		String serverCocTaskKey = getServerCocTaskKey(sid);
		dateStatisticRedis.increment(serverCocTaskKey, getServerCocTaskField(evTaskFinished), 1);
		dateStatisticRedis.expire(serverCocTaskKey, DATE_STATISTIC_EXPIRE_TIME);
	}

	private String getServerCocTaskField(EPTaskFinished evTaskFinished) {
		int level = evTaskFinished.getLevel();
		String field = "";
		if (level == 1) {
			field = "初级任务";
		}
		if (level == 2) {
			field = "中级任务";
		}
		if (level == 3) {
			field = "高级任务";
		}
		return field;
	}

	/**
	 * 区服特产统计的key
	 * @param sid
	 * @param awardName
	 * @return
	 */
	private String getSpecialStatisticKey(int sid, String awardName) {
		return getBaseDateStatisticKey(sid) + ServerRedisKey.SPLIT + awardName;
	}

	/**
	 *
	 * @param sid 区服id
	 * @param typeName 总成本/总消耗
	 * @param addNum
	 * @param awardName
	 */
	public void specialStatistic(int sid, String typeName, int addNum, String awardName) {
		dateStatisticRedis.increment(getSpecialStatisticKey(sid, awardName), typeName, addNum);
		dateStatisticRedis.expire(getSpecialStatisticKey(sid, awardName), DATE_STATISTIC_EXPIRE_TIME);
	}

	private String getServerDailyTaskKey(int sid) {
		return getBaseDateStatisticKey(sid) + ServerRedisKey.SPLIT + "每日任务";
	}

	public void dailyTaskStatistic(int sid, int score, int addNum) {
		dateStatisticRedis.increment(getServerDailyTaskKey(sid), String.valueOf(score), addNum);
		dateStatisticRedis.expire(getServerDailyTaskKey(sid), DATE_STATISTIC_EXPIRE_TIME);
	}

	public void loginStatistic(long uid, int sid, long addValue) {
		loginStatistic(uid, sid, DateUtil.now(), addValue);
	}

	public void loginStatistic(long uid, int sid, Date date, long addValue) {
		String key = getLoginStatisticKey(sid, date);
		redisZSetUtil.remove(key, uid);
		redisZSetUtil.incrementScore(key, uid, addValue);
		redisZSetUtil.expire(key, DATE_STATISTIC_EXPIRE_TIME);
	}

	public Set<Long> getLoginUids(int sid, Date date) {
		String key = getLoginStatisticKey(sid, date);
		return redisZSetUtil.range(key);
	}
}
