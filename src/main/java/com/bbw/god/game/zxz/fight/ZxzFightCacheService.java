package com.bbw.god.game.zxz.fight;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;


/**
 * 诛仙阵战斗缓存服务
 *
 * @author: suhq
 * @date: 2022/9/29 9:19 下午
 */
@Service
public class ZxzFightCacheService {
    private static String DEFENDER_CACHE_KEY_SUFFIX = "zxzRegionId";
    private static String ZXZ_FOUR_SAINTS_DEFENDERID = "zxzFourSaintsDefenderId";
    /** 上次刷新时间 */
    private static String LAST_REFRESH_DATE = "lastRefreshDate";
    /** 诛仙阵四圣挑战 上次刷新时间 */
    private static String FOUR_SAINTS_LAST_REFRESH_DATE = "fourSaintsLastRefreshDate";

    /**
     * 缓存挑战的守卫ID
     *
     * @param uid
     * @param defenderId
     */
    public void cacheDefenderId(Long uid, int defenderId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, DEFENDER_CACHE_KEY_SUFFIX, defenderId);
    }
    /**
     * 缓存四圣挑战的守卫ID
     *
     * @param uid
     * @param defenderId
     */
    public void cacheFourSaintsDefenderId(Long uid, int defenderId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, ZXZ_FOUR_SAINTS_DEFENDERID, defenderId);
    }

    /**
     * 缓存挑战时候上一次区域刷新时间
     * @param uid
     * @param lastRefreshDate
     */
    public void  cacheLastRefreshDate(long uid, long lastRefreshDate){
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, LAST_REFRESH_DATE, lastRefreshDate);

    }
    /**
     * 缓四圣挑战时候上一次区域刷新时间
     * @param uid
     * @param lastRefreshDate
     */
    public void  cacheFourSaintsLastRefreshDate(long uid, long lastRefreshDate){
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, FOUR_SAINTS_LAST_REFRESH_DATE, lastRefreshDate);

    }

    /**
     * 获取守卫ID
     *
     * @param uid
     * @return
     */
    public Integer getDefenderId(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, DEFENDER_CACHE_KEY_SUFFIX, Integer.class);
    }
    /**
     * 获取四圣挑战守卫ID
     *
     * @param uid
     * @return
     */
    public Integer getFourSaintsDefenderId(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, ZXZ_FOUR_SAINTS_DEFENDERID, Integer.class);
    }

    /**
     * 获取上次刷新时间
     * @param uid
     * @return
     */
    public Long getLastRefreshDate(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, LAST_REFRESH_DATE, Long.class);
    }
    /**
     * 获取四圣挑战上次刷新时间
     * @param uid
     * @return
     */
    public Long getFourSaintsLastRefreshDate(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, FOUR_SAINTS_LAST_REFRESH_DATE, Long.class);
    }


    /**
     * 获取区域ID
     *
     * @param uid
     * @return
     */
    public Integer getRegionId(long uid) {
        Integer defenderId = getDefenderId(uid);
        Integer regionId = ZxzTool.getRegionId(defenderId);
        return regionId;
    }


}
