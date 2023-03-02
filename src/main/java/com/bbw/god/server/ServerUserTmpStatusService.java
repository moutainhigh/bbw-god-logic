package com.bbw.god.server;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 玩家临时状态
 * 
 * @author suhq
 * @date 2019-08-28 15:24:17
 */
@Service
public class ServerUserTmpStatusService {
	public static String PUSH_FST_RANK_DOWN = "push" + UserRedisKey.SPLIT + "fstRankDown";
	public static String PUSH_SXDH_RANK_DOWN = "push" + UserRedisKey.SPLIT + "sxdhRankDown";
	public static String PUSH_FHB_RANK_DOWN = "push" + UserRedisKey.SPLIT + "fhbRankDown";
	@Autowired
	private RedisSetUtil<Long> tmpStatusSetUtil;

	/**
	 * 登录清除临时状态
	 * 
	 * @param sid
	 * @param uid
	 */
	public void clearTmpStatusOnLogin(int sid, long uid) {
		List<String> businessKeys = Arrays.asList(PUSH_FST_RANK_DOWN, PUSH_SXDH_RANK_DOWN, PUSH_FHB_RANK_DOWN);
		for (String businessKey : businessKeys) {
			tmpStatusSetUtil.remove(getRedisKey(sid, businessKey), uid);
		}
	}

	/**
	 * 临时状态是否已被设置
	 * 
	 * @param sid
	 * @param uid
	 * @param businessKey
	 */
	public boolean isSeted(int sid, long uid, String businessKey) {
		return tmpStatusSetUtil.isMember(getRedisKey(sid, businessKey), uid);
	}

	/**
	 * 设置临时状态
	 * 
	 * @param sid
	 * @param uid
	 * @param businessKey
	 */
	public void setTmpStatus(int sid, long uid, String businessKey) {
		tmpStatusSetUtil.add(getRedisKey(sid, businessKey), uid);
	}

	private String getRedisKey(int sid, String businessKey) {
		return ServerRedisKey.getRunTimeVarKey(sid, businessKey);
	}

}
