package com.bbw.god.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bbw.cache.LocalCache;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;

/**
 * 二级缓存。同时缓存本地和redis。
 * 基于缓存对象的obj.getClass().getSimpleName()作为数据分类，应保证不放入同短名称的对象。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-02 08:19
 */
@Component
public class L2Cache {
	private static RedisValueObjectUtil redis = SpringContextUtil.getBean(RedisValueObjectUtil.class);

	private static Long timeOut;// 缓存Session周期

	public static Long getTimeOut() {
		return timeOut;
	}

	@Value("${spring.session.timeout}")
	public void setTimeOut(Long timeOut) {
		L2Cache.timeOut = timeOut;
	}

	/**
	 * 数据缓存到本地和redis
	 * @param <T>
	 * @param uid
	 * @param dataType
	 * @param obj
	 */
	public static <T> void cacheBothLocalAndRedis(Long uid, String dataType, T obj) {
		String dataKey = UserRedisKey.getRunTimeVarKey(uid, dataType);
		LocalCache.getInstance().put(dataType, dataKey, obj, timeOut);
		redis.set(dataKey, obj, timeOut);
	}

	/**
	 * 移除缓存
	 * 
	 * @param uid
	 * @param clazz
	 */
	public static <T> void removeCache(long uid, String dataType) {
		String dataKey = UserRedisKey.getRunTimeVarKey(uid, dataType);
		LocalCache.getInstance().remove(dataType, dataKey);
		redis.delete(dataKey);
	}

	public static <T> boolean containsKey(long uid, String dataType) {
		String dataKey = UserRedisKey.getRunTimeVarKey(uid, dataType);
		return LocalCache.getInstance().containsKey(dataType, dataKey) || redis.exists(dataKey);
	}

	/**
	 * 从本地和redis获取数据
	 * 
	 * @param uid
	 * @param clazz
	 * @return
	 */
	public static <T> T getFromCache(Long uid, String dataType, Class<T> clazz) {
		String dataKey = UserRedisKey.getRunTimeVarKey(uid, dataType);
		T obj = LocalCache.getInstance().get(dataType, dataKey);
		if (null != obj) {
			return obj;
		}
		Object tmp = redis.get(dataKey);
		if (null != tmp) {
			return clazz.cast(tmp);
		}
		return null;
	}

}
