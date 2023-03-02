package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 寻藏宝图redis服务
 *
 * @author: huanghb
 * @date: 2022/2/8 14:53
 */
@Service
public class TreasureTroveMapDataService {

    /**
     * 更新用户藏宝图
     *
     * @param uid
     * @param userTreasureTroveMap
     */
    public void updateTreasureTroveMapToCache(long uid, UserTreasureTroveMap userTreasureTroveMap) {
        String cacheKey = getTreasureTroveMapCacheKey();
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, cacheKey, userTreasureTroveMap, DateUtil.SECOND_ONE_DAY * 5);
    }

    /**
     * 读取藏宝图信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserTreasureTroveMap getTreasureTroveMapFromCache(long uid) {
        String cacheKey = getTreasureTroveMapCacheKey();
        UserTreasureTroveMap userTreasureTroveMap = TimeLimitCacheUtil.getFromCache(uid, cacheKey, UserTreasureTroveMap.class);
        return userTreasureTroveMap;
    }


    /**
     * 藏宝秘境缓存key
     *
     * @return
     */
    private String getTreasureTroveMapCacheKey() {
        return "treasure" + SPLIT + "trove" + SPLIT + "map";
    }
}
