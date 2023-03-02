package com.bbw.god.gameuser.statistic.behavior.snatchtreasure;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 夺宝统计service
 * @date 2020/6/30 14:42
 */
@Service
public class SnatchTreasureStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.SNATCH_TREASURE_DRAW;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof SnatchTreasureStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        SnatchTreasureStatistic snatchTreasureStatistic = (SnatchTreasureStatistic) statistic;
        Integer date = snatchTreasureStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, snatchTreasureStatistic.getToday());
        map.put(TOTAL, snatchTreasureStatistic.getTotal());
        map.put(WEEK, snatchTreasureStatistic.getWeekDrawTimes());
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) getSnatchTreasureStatistic(date, redisMap);
    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return 统计对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public SnatchTreasureStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getSnatchTreasureStatistic(date, redisMap);
    }

    private SnatchTreasureStatistic getSnatchTreasureStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer week = redisMap.get(WEEK) == null ? 0 : redisMap.get(WEEK);
        return new SnatchTreasureStatistic(today, total, date, week);
    }

    public void draw(long uid, int date, int drawTimes) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, drawTimes);
        redisHashUtil.increment(key, TOTAL, drawTimes);
        redisHashUtil.increment(key, WEEK, drawTimes);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void resetPerWeek(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putField(key, WEEK, 0);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }


    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {

    }
}
