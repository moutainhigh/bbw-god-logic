package com.bbw.god.game.combat.cache;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.data.Player;

/**
 * 说明：战斗缓存工具类
 *
 * @author lwb
 * date 2021-04-25
 */
public class CombatCacheUtil {

    private static final Long REDIS_TIME_OUT = 3 * 60 * 60L;// 缓存3小时超时

    /**
     * 缓存中保存的标识
     * @param combatId
     * @return
     */
    public static String getCacheKey(long combatId){
        return CombatCache.class.getSimpleName()+":"+combatId;
    }

    /**
     * 获取战斗缓存
     * @param uid
     * @param combatId
     * @return
     */
    public static CombatCache getCombatCache(long uid,long combatId){
        CombatCache fromCache =null;
        if (uid<0){
            fromCache=new CombatCache();
            fromCache.setCombatId(combatId);
            fromCache.setUid(uid);
            return fromCache;
        }
        fromCache = TimeLimitCacheUtil.getFromCache(uid, getCacheKey(combatId), CombatCache.class);
        if (fromCache==null){
            fromCache=new CombatCache();
            fromCache.setCombatId(combatId);
            fromCache.setUid(uid);
        }
        return fromCache;
    }
    public static CombatCache getCombatCache(Player player){
        return getCombatCache(player.getUid(),player.getCombatId());
    }
    /**
     * 设置战斗缓存:默认保存3小时
     * @param cache
     */
    public static void setCombatCache(CombatCache cache){
        if (cache.getUid()<0 || cache.getCombatId()<0){
            return;
        }
        TimeLimitCacheUtil.cacheBothLocalAndRedis(cache.getUid(),getCacheKey(cache.getCombatId()),cache,REDIS_TIME_OUT);
    }
}
