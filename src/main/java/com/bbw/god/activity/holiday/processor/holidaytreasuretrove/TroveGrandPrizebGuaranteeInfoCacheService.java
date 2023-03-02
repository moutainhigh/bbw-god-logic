package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 大奖保底信息缓存
 *
 * @author: huanghb
 * @date: 2022/7/11 11:58
 */
@Service
public class TroveGrandPrizebGuaranteeInfoCacheService {
    @Autowired
    private RedisHashUtil<Integer, Integer> guaranteeInfoRedisUtil;
    /** 缓存时间 */
    public final static Integer CACHE_TIMES = DateUtil.SECOND_ONE_DAY * 11;

    /**
     * 获取奖励次数
     *
     * @param uid
     */
    public void renewGuaranteeInfo(long uid, int prizePoolId) {
        String cacheKey = getCacheKey(uid);
        int guaranteeInfo = getGuaranteeInfo(uid, prizePoolId);
        guaranteeInfo++;
        guaranteeInfoRedisUtil.putField(cacheKey, prizePoolId, guaranteeInfo, CACHE_TIMES);
    }

    /**
     * 读取藏宝秘境信息（从缓存）
     *
     * @param uid
     * @return
     */
    public Integer getGuaranteeInfo(long uid, int prizePoolId) {
        String cacheKey = getCacheKey(uid);
        Integer guaranteeInfo = guaranteeInfoRedisUtil.getField(cacheKey, prizePoolId);
        if (null == guaranteeInfo) {
            guaranteeInfo = 0;
            guaranteeInfoRedisUtil.putField(cacheKey, prizePoolId, guaranteeInfo, CACHE_TIMES);
        }
        return guaranteeInfo;
    }

    /**
     * 特殊大奖购买次数缓存key
     *
     * @return
     */
    private String getCacheKey(long uid) {
        return "treasure" + SPLIT + "trove" + SPLIT + "guaranteeInfo" + SPLIT + uid;
    }
}
