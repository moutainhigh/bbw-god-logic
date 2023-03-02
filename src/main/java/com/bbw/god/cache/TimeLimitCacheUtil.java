package com.bbw.god.cache;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.yed.RDYeDEventCache;
import com.bbw.god.city.yeg.RDYeGuaiEliteBox;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.dfdj.fight.DfdjFightCache;
import com.bbw.god.gameuser.leadercard.CacheLeaderCard;
import com.bbw.god.gameuser.redis.UserRedisKey;

import java.util.List;

public class TimeLimitCacheUtil {
    private static final Long REDIS_TIME_OUT = 3 * 60 * 60L;// 缓存3小时超时
    private static final Long LOCAL_TIME_OUT = 5 * 60L;// 本地缓存5分钟
    private static RedisValueObjectUtil redis = SpringContextUtil.getBean(RedisValueObjectUtil.class);

    /**
     * 摇骰子到达位置临时缓存
     *
     * @param uid
     * @param info
     */
    public static <T extends RDCityInfo> void setArriveCache(Long uid, T info) {
        String typeKey = info.getClass().getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, info);
    }

    /**
     * 摇骰子到达位置临时缓存
     *
     * @param uid
     * @return
     */
    public static <T extends RDCityInfo> T getArriveCache(long uid, Class<T> clazz) {
        T info = getFromCache(uid, clazz);
        if (info == null) {
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        return info;
    }

    /**
     * 获得城池信息
     *
     * @param uid
     * @return
     */
    public static RDArriveChengC getChengCCache(long uid) {
        RDArriveChengC info = getFromCache(uid, RDArriveChengC.class);
        if (info == null) {
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        return info;
    }

    /**
     * 设置城池信息缓存（仅仅是城池）
     *
     * @param uid
     * @return
     */
    public static void setChengChiInfoCache(long uid, ChengChiInfoCache infoCache) {
        String typeKey = infoCache.getClass().getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, infoCache);
    }

    public static ChengChiInfoCache getChengChiInfoCache(long uid) {
        ChengChiInfoCache info = getFromCache(uid, ChengChiInfoCache.class);
        if (info == null) {
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        return info;
    }

    /**
     * 战斗结束后的临时缓存
     *
     * @param uid
     * @return
     */
    public static RDFightResult getFightResultCache(Long uid) {
        RDFightResult result = getFromCache(uid, RDFightResult.class);
        return result;
    }

    /**
     * 战斗结束后的临时缓存
     *
     * @param uid
     * @param result
     */
    public static void setFightResultCache(Long uid, RDFightResult result) {
        String typeKey = RDFightResult.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, result);
    }

    /**
     * 获取野怪宝箱缓存
     *
     * @param uid
     * @return
     */
    public static RDYeGuaiEliteBox getYeGuaiBoxCache(long uid) {
        return getFromCache(uid, RDYeGuaiEliteBox.class);
    }

    /**
     * 野怪宝箱缓存
     *
     * @param uid
     * @return
     */
    public static void setYeGuaiBoxCache(long uid, List<Award> awards) {
        RDYeGuaiEliteBox yeGuaiBoxCache = getYeGuaiBoxCache(uid);
        if (null == yeGuaiBoxCache) {
            yeGuaiBoxCache = new RDYeGuaiEliteBox();
        }
        yeGuaiBoxCache.addAwards(awards);
        String typeKey = RDYeGuaiEliteBox.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, yeGuaiBoxCache);
    }

    /**
     * 战斗结束后的临时缓存
     *
     * @param uid
     * @return
     */
    public static RDFightEndInfo getFightEndCache(Long uid) {
        RDFightEndInfo info = getFromCache(uid, RDFightEndInfo.class);
        return info;
    }

    /**
     * 战斗结束后的临时缓存
     *
     * @param uid
     * @param info
     */
    public static void setFightEndCache(Long uid, RDFightEndInfo info) {
        String typeKey = RDFightEndInfo.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, info);
    }

    /**
     * 交易数据缓存
     *
     * @param uid
     * @return
     */
    public static RDTradeInfo getTradInfoCache(Long uid) {
        RDTradeInfo info = getFromCache(uid, RDTradeInfo.class);
        if (info == null) {
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        return info;
    }

    /**
     * 交易数据缓存
     *
     * @param uid
     * @param info
     */
    public static void setTradInfoCache(Long uid, RDTradeInfo info) {
        String typeKey = RDTradeInfo.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, info);
        //去除返回参数中的详细信息
        info.getSellingSpecials().forEach(p -> {
            p.setSellPriceParam(null);
        });
    }

//    /**
//     * 获得使用战斗法宝的缓存
//     *
//     * @param uid
//     * @return
//     */
//    public static RDUseFightTreasure getFightTreasureUsedCache(Long uid) {
//        RDUseFightTreasure info = getFromCache(uid, RDUseFightTreasure.class);
//        return info;
//    }
//
//    /**
//     * 设置使用战斗法宝的缓存
//     *
//     * @param uid
//     * @param info
//     */
//    public static void setFightTreasureUsedCache(Long uid, RDUseFightTreasure info) {
//        String typeKey = RDUseFightTreasure.class.getSimpleName();
//        cacheBothLocalAndRedis(uid, typeKey, info);
//    }

    /**
     * 获取战斗成就数据缓存
     *
     * @param uid
     * @return
     */
    public static FightAchievementCache getFightAchievementCache(Long uid) {
        if (uid < 0) {
            return null;
        }
        FightAchievementCache info = getFromCache(uid, FightAchievementCache.class);
        return info;
    }

    public static FightAchievementCache getOrCreateFightAchievementCache(Long uid, long combatId) {
        if (uid < 0) {
            return null;
        }
        FightAchievementCache info = getFightAchievementCache(uid);
        if (info == null || info.getCombatId() != combatId) {
            info = new FightAchievementCache();
            info.setCombatId(combatId);
            setFightAchievementCache(uid, info);
        }
        return info;
    }

    public static void setFightAchievementCache(Long uid, FightAchievementCache info) {
        if (uid < 0) {
            return;
        }
        String typeKey = FightAchievementCache.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, info);
    }

    public static void setDfdjFightCache(long uid, DfdjFightCache cache) {
        if (uid < 0) {
            return;
        }
        String typeKey = DfdjFightCache.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, cache);
    }

    public static DfdjFightCache getDfdjFightCache(long uid) {
        return getFromCache(uid, DfdjFightCache.class);
    }

    public static RDYeDEventCache getYeDEventCache(long uid) {
        RDYeDEventCache cache = getFromCache(uid, RDYeDEventCache.class);
        if (null == cache) {
            cache = new RDYeDEventCache();
            setYeDEventCache(uid, cache);
        }
        return cache;
    }

    public static void setYeDEventCache(long uid, RDYeDEventCache cache) {
        String typeKey = RDYeDEventCache.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, cache);
    }

    /**
     * 获取主角卡的相关缓存
     * @param uid
     * @return
     */
    public static CacheLeaderCard getLeaderCardCache(long uid){
        return getFromCache(uid, CacheLeaderCard.class);
    }

    /**
     * 设置主角卡缓存
     *
     * @param uid
     * @param cache
     */
    public static void setLeaderCardCache(long uid, CacheLeaderCard cache) {
        String typeKey = CacheLeaderCard.class.getSimpleName();
        cacheBothLocalAndRedis(uid, typeKey, cache);
    }


    /**
     * 移除缓存
     *
     * @param guId
     * @param obj
     */
    public static <T> void setCache(long guId, T obj) {
        String typeKey = obj.getClass().getSimpleName();
        cacheBothLocalAndRedis(guId, typeKey, obj);
    }


    /**
     * 移除缓存
     *
     * @param guId
     * @param clazz
     */
    public static <T> void removeCache(long guId, Class<T> clazz) {
        String typeKey = clazz.getSimpleName();
        cacheBothLocalAndRedis(guId, typeKey, null);
    }

    /**
     * 数据缓存到本地和redis
     *
     * @param uid
     * @param suffixKey
     * @param obj
     */
    public static <T> void cacheBothLocalAndRedis(Long uid, String suffixKey, T obj) {
        cacheBothLocalAndRedis(uid, suffixKey, obj, REDIS_TIME_OUT);
    }

    public static <T> void cacheBothLocalAndRedis(Long uid, String suffixKey, T obj, long secondsLimit) {
        String dataKey = UserRedisKey.getRunTimeVarKey(uid, suffixKey);
        if (null == obj) {
            redis.delete(dataKey);
        }
//        if (null == obj) {
//            LocalCache.getInstance().remove(suffixKey, dataKey);
//            return;
//        }
        // TODO 可能引起异常，注释掉观察一段时间
        // LocalCache.getInstance().put(dataType, dataKey, obj, LOCAL_TIME_OUT);
        redis.set(dataKey, obj, secondsLimit);
    }

    /**
     * 从本地和redis获取数据
     *
     * @param uid
     * @param clazz
     * @return
     */
    public static <T> T getFromCache(Long uid, Class<T> clazz) {
        String dataType = clazz.getSimpleName();
        return getFromCache(uid, dataType, clazz);
    }

    public static <T> T getFromCache(Long uid, String suffixKey, Class<T> clazz) {
        String dataKey = UserRedisKey.getRunTimeVarKey(uid, suffixKey);
        // TODO 可能引起异常，注释掉观察一段时间
        // T obj = LocalCache.getInstance().get(dataType, dataKey);
        // if (null != obj) {
        // return obj;
        // }
        Object tmp = redis.get(dataKey);
        if (null != tmp) {
            return clazz.cast(tmp);
        }
        return null;
    }

}
