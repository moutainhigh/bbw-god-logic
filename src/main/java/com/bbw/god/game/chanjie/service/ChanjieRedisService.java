package com.bbw.god.game.chanjie.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisListUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.chanjie.ChanjieTools;
import com.bbw.god.game.chanjie.ChanjieType;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月16日 下午4:53:35 
* 类说明 
*/
@Service
public class ChanjieRedisService {
	@Autowired
	private RedisListUtil<String> fightLogList;
	@Autowired
	private RedisHashUtil<String, Long> hashUtil;
	@Autowired
	private RedisZSetUtil<Long> rankingList;// score分值：玩家排行，member：玩家ID

	private static int timeout=15;//过期时间
	private TimeUnit timeoutUnit=TimeUnit.DAYS;//单位
	/**
	 * 获取战斗日志列表
	 * @param type 教派
	 * @param current 当前页
	 * @param limit 页面大小
	 * @param gid 平台
	 * @return
	 */
	public List<String> getFightLogs(ChanjieType type, int limit, int current, int gid) {
		String key = ChanjieTools.getDailyKey(type, new Date(), ChanjieType.KEY_FIGHT_LOG_ZSET, gid);
		int start = (current - 1) * limit;
		int end = current * limit;
		List<String> logs = fightLogList.get(key, start, end);
		return logs;
	}
	
	public void fightLogLeftPush(ChanjieType type,String log,int gid) {
		String key = ChanjieTools.getDailyKey(type, new Date(), ChanjieType.KEY_FIGHT_LOG_ZSET, gid);
		fightLogList.leftPush(key,log);
		fightLogList.expire(key, timeout,timeoutUnit);
	}
	/**
	 * 获取榜单列表
	 * @param keyString  redis存储的key值
	 * @param current
	 * @param limit
	 * @return
	 */
	public Set<Long> getRankingLists(String keyString,int current,int limit){
		int start = (current - 1) * limit;
		int end = current * limit - 1;
		return rankingList.reverseRange(keyString,start,end);
	}
	/**
	 * 通过指定索引获取榜单
	 * @param keyString
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<Long> getRankingListsByindex(String keyString,Long start,Long end){
		return rankingList.reverseRange(keyString,start,end);
	}
	/**
	 * 通过key获取整个榜单
	 * @param keyString
	 * @return
	 */
	public Set<Long> getAllRankingLists(String keyString){
		return rankingList.reverseRange(keyString);
	}
	/**
	 * 获取玩家排行
	 * @param keyString
	 * @param uid
	 * @return
	 */
	public Long getRanking(String keyString,Long uid) {
		Long rank = rankingList.reverseRank(keyString, uid);
		return rank;
	}
	/**
	 * 获取榜单大小
	 * @param keyStr
	 * @return
	 */
	public Long getZSetSize(String keyStr) {
		return rankingList.size(keyStr);
	}
	/**
	 * 获取榜单中的分数
	 * @param key 
	 * @param uid 指定的玩家
	 * @return
	 */
	public double getZSetScore(String key,long uid) {
		double val= rankingList.score(key, uid);
		return val;
	}
	/**
	 * 增加榜单的分数
	 * @param key
	 * @param addScore 增量
	 * @param uid
	 */
	public void incrementScore(String key,long addScore,long uid) {
		rankingList.incrementScore(key, uid, addScore);
		rankingList.expire(key, timeout,timeoutUnit);
	}
	/**
	 * 移除榜单
	 * @param key
	 * @param gid
	 * @param uid
	 */
	public void removeZset(String key,Long...uid) {
		rankingList.remove(key, uid);
	}
	/**
	 * 添加到榜单中 单个
	 * @param key
	 * @param gid
	 * @param val
	 * @param uid
	 */
	public void addZset(String key,Long val,Long uid) {
		rankingList.add(key, uid, val);
		rankingList.expire(key, timeout,timeoutUnit);
	}
	/**
	 * 添加到榜单中  多个
	 * @param key
	 * @param gid
	 * @param val
	 * @param uid
	 */
	public void addZsets(String key,double[] val,Long[] uid) {
		rankingList.add(key, val, uid);
		rankingList.expire(key, timeout,timeoutUnit);
	}
	/**
	 * 添加数据到hash
	 * @param key
	 * @param firld
	 * @param val
	 */
	public void putHash(String key,String firld,long val) {
		hashUtil.putField(key, firld, val);
		hashUtil.expire(key, timeout,timeoutUnit);
		
	}
	/**
	 * 获取hash值
	 * @param key
	 * @param fieldKey
	 * @return
	 */
	public Long getHashVal(String key,String fieldKey) {
		Object object = hashUtil.getField(key, fieldKey);
		if (object == null) {
			return 0l;
		}
		Long value = Long.parseLong(object.toString());
		return value;
	}
	/**
	 * 增加 hash的值
	 * @param key
	 * @param fieldKey
	 * @param by 差值
	 * @return
	 */
	public long incHashVal(String key,String fieldKey,int by) {
		hashUtil.expire(key, timeout,timeoutUnit);
		return hashUtil.increment(key,fieldKey, by);
	}
	/**
	 * 减少hash的值
	 * @param key
	 * @param fieldKey
	 * @param by  差值（负数）
	 * @return
	 */
	public long decHashVal(String key,String fieldKey,int by) {
		hashUtil.expire(key,timeout,timeoutUnit);
		return hashUtil.decrement(key,fieldKey, by);
	}
	
}
