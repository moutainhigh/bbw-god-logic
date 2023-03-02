package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 大奖购买次数缓存
 *
 * @author: suhq
 * @date: 2021/12/24 9:29 上午
 */
@Service
public class TroveGrandPrizeBuyTimesCacheService {
    @Autowired
    private RedisHashUtil<Integer, Integer> buyTimesRedisUtil;

    public final static Integer TOTAL_BUY_TIMES = 0;

    /**
     * 获取购买次数
     *
     * @param uid
     */
    public void addBuyTimes(long uid, int prizePoolId) {
        String cacheKey = getCacheKey(uid);
        int awardedNum = getBuyTimes(uid, prizePoolId);
        awardedNum++;
        if (prizePoolId != 0) {
            addBuyTimes(uid, TOTAL_BUY_TIMES);
        }
        buyTimesRedisUtil.putField(cacheKey, prizePoolId, awardedNum, DateUtil.SECOND_ONE_DAY * 11);
    }

    /**
     * 获取购买次数
     *
     * @param uid
     */
    public void addBuyTimes(long uid, int prizePoolId, int buyTimes) {
        String cacheKey = getCacheKey(uid);
        if (prizePoolId != 0) {
            int perBuyTimes = getBuyTimes(uid, prizePoolId);
            int totalBuyTimes = getBuyTimes(uid, TOTAL_BUY_TIMES);
            totalBuyTimes += buyTimes - perBuyTimes;
            addBuyTimes(uid, TOTAL_BUY_TIMES, totalBuyTimes);
        }
        buyTimesRedisUtil.putField(cacheKey, prizePoolId, buyTimes, DateUtil.SECOND_ONE_DAY * 11);
    }


    /**
     * 读取购买次数（从缓存）
     *
     * @param uid
     * @return
     */
    public Integer getBuyTimes(long uid, int prizePoolId) {
        String cacheKey = getCacheKey(uid);

        Integer awardedNum = buyTimesRedisUtil.getField(cacheKey, prizePoolId);
        return null == awardedNum ? 0 : awardedNum;
    }

    /**
     * 读取所有藏宝购买次数（从缓存）
     *
     * @param uid
     * @return
     */
    public Map<Integer, Integer> getAllBuyTimes(long uid) {
        String cacheKey = getCacheKey(uid);
        return buyTimesRedisUtil.get(cacheKey);
    }


    /**
     * 大奖购买次数缓存key
     *
     * @return
     */
    private String getCacheKey(long uid) {
        return "treasure" + SPLIT + "trove" + SPLIT + "buyTimes" + SPLIT + uid;
    }
}
