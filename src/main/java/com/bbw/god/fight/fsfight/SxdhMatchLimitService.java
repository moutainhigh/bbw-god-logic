package com.bbw.god.fight.fsfight;

import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.sxdh.SxdhDateService;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 特殊赛季：每个阶段最多进行50次匹配
 *
 * @author: suhq
 * @date: 2022/1/14 11:42 上午
 */
@Service
public class SxdhMatchLimitService {
    /** 数据有效期 */
    private static final int EXPIRED_SECONDS = 35 * 24 * 3600;
    @Autowired
    private SxdhDateService sxdhDateService;

    /**
     * 玩家是否可匹配
     *
     * @param uid
     * @return
     */
    public boolean isAbleMatch(long uid, SxdhZone sxdhZone) {
        return getRemainMatchTimes(uid, sxdhZone) > 0;
    }

    /**
     * 获取已匹配的次数
     *
     * @param uid
     * @param sxdhZone
     * @return
     */
    public Integer getMatchedTimes(long uid, SxdhZone sxdhZone) {
        String key = getKey(uid, sxdhZone);
        Integer matchedTimes = GameDataTimeLimitCacheUtil.getFromCache(key, Integer.class);
        return null == matchedTimes ? 0 : matchedTimes;
    }

    /**
     * 获得剩余匹配次数
     *
     * @param uid
     * @param sxdhZone
     * @return
     */
    public Integer getRemainMatchTimes(long uid, SxdhZone sxdhZone) {
        Integer matchedTimes = getMatchedTimes(uid, sxdhZone);
        int remainTimes = 50 - matchedTimes;
        return remainTimes >= 0 ? remainTimes : 0;
    }

    /**
     * 添加匹配记录
     *
     * @param uid
     * @param sxdhZone
     */
    public void addMatchedTimes(long uid, SxdhZone sxdhZone) {
        Integer matchedTimes = getMatchedTimes(uid, sxdhZone);
        String key = getKey(uid, sxdhZone);
        GameDataTimeLimitCacheUtil.cache(key, matchedTimes + 1, EXPIRED_SECONDS);
    }

    /**
     * 每10分钟对应一个key
     *
     * @return
     */
    private String getKey(long uid, SxdhZone sxdhZone) {
        StringBuilder sb = new StringBuilder();
        sb.append(GameRedisKey.getRunTimeVarKey("sxdhPhaseMatch"));
        sb.append(RedisKeyConst.SPLIT + sxdhZone.getSeason());
        CfgSxdh.SeasonPhase curSeasonPhase = sxdhDateService.getCurSeasonPhase();
        sb.append(RedisKeyConst.SPLIT + curSeasonPhase.getId());
        sb.append(RedisKeyConst.SPLIT + uid);
        return sb.toString();
    }

}
