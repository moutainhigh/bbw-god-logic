package com.bbw.god.cache;

import com.bbw.cache.LocalCache;
import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分享缓存
 * 
 * @author suhq
 * @date 2019年4月1日 下午6:41:30
 */
public class ShareCacheUtil {

	@Getter
	@AllArgsConstructor
	public static enum ShareType {
		CARD("分享卡牌", "sharecard"), ATTACK_CHENGC("攻占城池", "shareattack");
		private String name;
		private String redisKey;
	}

	@Getter
	@AllArgsConstructor
	public static enum ShareStatus {
		NO_AWARD("没有分享奖励", -1), ENABLE_AWARD("未分享获取奖励", 0);
		private String name;
		private Integer value;

		public static ShareStatus fromValue(int value) {
			for (ShareStatus item : values()) {
				if (item.getValue() == value) {
					return item;
				}
			}
			return null;
		}
	}

	private static final Long TIME_OUT = 15 * 60L;// 缓存15分钟
	private static RedisValueObjectUtil redis = SpringContextUtil.getBean(RedisValueObjectUtil.class);

	/**
	 * 设置卡牌分享状态
	 * 
	 * @param guId
	 * @param cardId
	 * @param status
	 */
	public static void setShareableCard(Long guId, Integer cardId, ShareStatus status) {
		String dataKey = getShareableRedisKey(guId, ShareType.CARD, cardId);
		cacheBothLocalAndRedis(guId, dataKey, status);
	}

	/**
	 * 获得卡牌分享状态
	 * 
	 * @param guId
	 * @param cardId
	 * @return
	 */
	public static ShareStatus getShareableCard(long guId, Integer cardId) {
		String dataKey = getShareableRedisKey(guId, ShareType.CARD, cardId);
		ShareStatus status = getStatusFromCache(guId, dataKey);
		return status;
	}

	/**
	 * 设置攻占城池的分享状态
	 * 
	 * @param guId
	 * @param status
	 */
	public static void setShareableAttack(Long guId, ShareStatus status) {
		String dataKey = getShareableRedisKey(guId, ShareType.ATTACK_CHENGC);
		cacheBothLocalAndRedis(guId, dataKey, status);
	}

	/**
	 * 获得攻占城池的分享状态
	 * 
	 * @param guId
	 * @return
	 */
	public static ShareStatus getShareableAttack(long guId) {
		String dataKey = getShareableRedisKey(guId, ShareType.ATTACK_CHENGC);
		ShareStatus status = getStatusFromCache(guId, dataKey);
		return status;
	}

	/**
	 * 分享状态缓存到本地和redis
	 * 
	 * @param uid
	 * @param dataType
	 * @param obj
	 */
	public static void cacheBothLocalAndRedis(Long uid, String dataKey, ShareStatus status) {
		if (status != ShareStatus.ENABLE_AWARD) {
			LocalCache.getInstance().remove(dataKey);
			return;
		}
		LocalCache.getInstance().put(dataKey, status.getValue(), TIME_OUT);
		redis.set(dataKey, status, TIME_OUT);
	}

	/**
	 * 从本地和redis获取分享状态
	 * 
	 * @param uid
	 * @param clazz
	 * @return
	 */
	public static ShareStatus getStatusFromCache(Long uid, String dataKey) {
		Integer status = LocalCache.getInstance().get(dataKey);
		if (null != status) {
			return ShareStatus.fromValue(status);
		}
		Object tmp = redis.get(dataKey);
		if (null != tmp) {
			status = StrUtil.getInt(tmp);
			return ShareStatus.fromValue(status);
		}
		return ShareStatus.NO_AWARD;
	}

	/**
	 * 分享key = urs:玩家ID:分享类型:分享标识
	 * 
	 * @param guId
	 * @param shareType
	 * @param mark
	 * @return
	 */
	private static String getShareableRedisKey(long guId, ShareType shareType, Integer mark) {
		return getShareableRedisKey(guId, shareType) + UserRedisKey.SPLIT + mark;
	}

	/**
	 * 分享key = urs:玩家ID:分享类型
	 * 
	 * @param guId
	 * @param shareType
	 * @return
	 */
	private static String getShareableRedisKey(long guId, ShareType shareType) {
		return UserRedisKey.getRunTimeVarKey(guId, shareType.getRedisKey());
	}
}
