package com.bbw.god.cache;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.data.redis.GameRedisKey;

/**
 * 全局数据Redis缓存
 *
 * @author: suhq
 * @date: 2021/10/20 4:48 下午
 */
public class GameDataTimeLimitCacheUtil {
    private static final Long REDIS_TIME_OUT = 3 * 60 * 60L;// 缓存3小时超时

    private static RedisValueObjectUtil redis = SpringContextUtil.getBean(RedisValueObjectUtil.class);


    /**
     * 移除缓存
     *
     * @param clazz
     */
    public static <T> void removeCache(Class<T> clazz) {
        String typeKey = clazz.getSimpleName();
        cache(typeKey, null);
    }

    /**
     * 数据缓存到redis,有效期为3小时
     *
     * @param suffixKey
     * @param obj
     */
    public static <T> void cache(String suffixKey, T obj) {
        cache(suffixKey, obj, REDIS_TIME_OUT);
    }

    /**
     * 数据缓存到redis
     *
     * @param suffixKey
     * @param obj
     * @param secondsLimit
     * @param <T>
     */
    public static <T> void cache(String suffixKey, T obj, long secondsLimit) {
        String dataKey = GameRedisKey.getRunTimeVarKey(suffixKey);
        if (null == obj) {
            redis.delete(dataKey);
        }
        redis.set(dataKey, obj, secondsLimit);
    }

    /**
     * 从redis获取缓存数据
     *
     * @param clazz
     * @return
     */
    public static <T> T getFromCache(Class<T> clazz) {
        String dataType = clazz.getSimpleName();
        return getFromCache(dataType, clazz);
    }

    /**
     * 从redis获取缓存数据
     *
     * @param suffixKey
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getFromCache(String suffixKey, Class<T> clazz) {
        String dataKey = GameRedisKey.getRunTimeVarKey(suffixKey);
        Object tmp = redis.get(dataKey);
        if (null != tmp) {
            return clazz.cast(tmp);
        }
        return null;
    }

}
