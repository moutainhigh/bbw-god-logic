package com.bbw.god.rechargeactivities.processor.dailyshake;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.CfgDailyShake;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 每日摇一摇缓存福利信息
 *
 * @author: huanghb
 * @date: 2022/6/16 16:40
 */
@Service
public class DailyShakeService {
    /**
     * 获得福利信息key
     *
     * @param uid
     * @return
     */
    private String getWelfareKey(long uid) {
        return "game" + SPLIT + uid + SPLIT + "dailyShake";
    }

    /**
     * 获得福利信息
     *
     * @param uid
     * @return
     */
    public CfgDailyShake.Welfare getWelfare(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, getWelfareKey(uid), CfgDailyShake.Welfare.class);
    }

    /**
     * 设置福利信息
     *
     * @param uid
     * @param welfare
     */
    public void setWelfare(long uid, CfgDailyShake.Welfare welfare) {
        int expirationTime = DateUtil.getTimeToNextDay();
        if (expirationTime <= 0) {
            return;
        }
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getWelfareKey(uid), welfare, expirationTime / 1000);
    }
}
