package com.bbw.mc.push;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 推送TOKEN服务
 * 
 * @author suhq
 * @date 2019-08-20 09:19:27
 */
@Slf4j
@Service
public class PushReceiverService {
	private static String REDIS_KEY = "pushReceiver";
	private static Long EXPIRE_TIME = 30*24*3600L;
	@Autowired
	private RedisValueUtil<PushReceiver> redisValueUtil;

	public void cachePushReceiver(Long uid, String pushToken,Integer channelId) {
		String key = getRedisKey(uid);
		PushReceiver receiver = new PushReceiver(pushToken,channelId);
		redisValueUtil.set(key,receiver,EXPIRE_TIME);
	}

	public PushReceiver getPushReceiver(Long uid) {
		String key = getRedisKey(uid);
		return redisValueUtil.get(key);
	}

	private String getRedisKey(long uid) {
		return GameRedisKey.getRunTimeVarKey(REDIS_KEY) + GameRedisKey.SPLIT + uid;
	}

}
