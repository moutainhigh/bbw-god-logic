package com.bbw.god.rechargeactivities.service;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.rechargeactivities.data.WeeklyRedisCacheData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author lwb
 */
@Service
public class RechargeActivityRedisService {
    @Autowired
    private RedisValueUtil<WeeklyRedisCacheData> redisValueUtil;
    private static final String BASE_KE="recharge:lianJiGiftPack:";
    /**
     * 添加
     *
     * @param awards
     */
    public void saveWeeklyLianJiAwards(List<Award> awards, List<Award> awards2) {
        int day = DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(DateUtil.now()));
        String key = getWeeklyLianJiAwardsKey();
        WeeklyRedisCacheData weeklyRedisCacheData = new WeeklyRedisCacheData();
        weeklyRedisCacheData.setAwards(awards);
        weeklyRedisCacheData.setAwards2(awards2);
        weeklyRedisCacheData.setId(day);
        redisValueUtil.set(key, weeklyRedisCacheData, 10, TimeUnit.DAYS);
    }

    public void updateWeeklyLianJiAwards(WeeklyRedisCacheData weeklyRedisCacheData) {
        String key = getWeeklyLianJiAwardsKey();
        redisValueUtil.set(key, weeklyRedisCacheData, 10, TimeUnit.DAYS);
    }


    public Optional<WeeklyRedisCacheData> getWeeklyLianJiAwards() {
        WeeklyRedisCacheData cacheData = redisValueUtil.get(getWeeklyLianJiAwardsKey());
        if (cacheData == null) {
            return Optional.empty();
        }
        return Optional.of(cacheData);
    }

    private String getWeeklyLianJiAwardsKey() {
        int day= DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(DateUtil.now()));
        return BASE_KE+day;
    }
}
