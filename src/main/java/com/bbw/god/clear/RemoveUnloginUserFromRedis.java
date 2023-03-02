package com.bbw.god.clear;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.server.ServerUserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 移除未登录但加载到Redis中的玩家的数据
 * 
 * @author suhq
 * @date 2019-07-12 09:01:18
 */
@Slf4j
@Service
public class RemoveUnloginUserFromRedis implements GameClearService {
	private static int unLoginDays = 13;// redis中加载过的的多少天内未登录的用户数据
	private static int expireSeconds = 3 * 24 * 3600;
	@Autowired
	private RedisSetUtil<Long> uidSetRedis;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ServerUserService serverUserService;

	/**
	 * 加入删除队列
	 * 
	 * @param uid
	 */
	public void toAddRemoveSet(long uid) {
		Date now = DateUtil.now();
		UserLoginInfo userLoginInfo = gameUserService.getSingleItem(uid, UserLoginInfo.class);
		if (userLoginInfo != null && userLoginInfo.getLastLoginTime() != null) {
			if (DateUtil.getDaysBetween(userLoginInfo.getLastLoginTime(), now) >= unLoginDays) {
				String redisKey = getKey(DateUtil.toDateInt(now));
				// 设置过期时间
				if (!uidSetRedis.exists(redisKey)) {
					log.info("设置{}过期时间{}", redisKey, expireSeconds);
					uidSetRedis.expire(redisKey, expireSeconds);
				}
				// 加入删除队列
				uidSetRedis.add(redisKey, uid);

			}

		}
	}

	@Override
	public void clear() {
		Date now = DateUtil.now();
		Date yesterday = DateUtil.addDays(now, -1);
		String yeterdayKey = getKey(DateUtil.toDateInt(yesterday));
		Date dateBefore = DateUtil.addDays(now, -2);
		String daysBeforeKey = getKey(DateUtil.toDateInt(dateBefore));
		// 清除昨天有需要，前天不需要的数据
		Set<Long> uidSet = uidSetRedis.difference(yeterdayKey, daysBeforeKey);
		int size = 0;
		if (SetUtil.isNotEmpty(uidSet)) {
			size = uidSet.size();
			List<Long> uidList = uidSet.stream().collect(Collectors.toList());
			log.info("移除未登录但加载到Redis中的玩家的数:" + uidList.size());
			serverUserService.unloadGameUsers(uidList, "未指定");
		}
		log.info("移除未登录但加载到Redis中的玩家的数:" + size);
	}

	private String getKey(int dateInt) {
		return GameRedisKey.getRunTimeVarKey("loadedOldUser") + GameRedisKey.SPLIT + dateInt;
	}

}
