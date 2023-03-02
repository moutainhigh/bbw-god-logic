package com.bbw.god.gameuser.statistic.resource.gold;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 元宝统计service
 * @date 2020/4/18 9:50
 **/
@Service
public class GoldResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;

    /**
     * 获取类型总数，例：城池统计只有获得，没有消耗，返回1 元宝统计，有获得也有消耗，返回2
     *
     * @return 类型总数
     */
    @Override
    public int getMyTypeCount() {
        return 2;
    }

    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.YB;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof GoldStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        GoldStatistic goldStatistic = (GoldStatistic) statistic;
        Map<String, Integer> map = new HashMap<>(16);
        Integer todayNum = goldStatistic.getToday();
        Integer totalNum = goldStatistic.getTotal();
        int type = goldStatistic.getType();
        Integer date = goldStatistic.getDate();
        map.put(date + UNDERLINE + NUM, todayNum);
        map.put(TOTAL, totalNum);
        Map<WayEnum, Integer> todayMap = goldStatistic.getTodayMap();
        Set<WayEnum> todayKeySet = todayMap.keySet();
        for (WayEnum todayWay : todayKeySet) {
            map.put(date + UNDERLINE + todayWay.getName(), todayMap.get(todayWay));
        }
        Map<WayEnum, Integer> totalMap = goldStatistic.getTotalMap();
        Set<WayEnum> totalKeySet = totalMap.keySet();
        for (WayEnum totalWay : totalKeySet) {
            map.put(totalWay.getName(), totalMap.get(totalWay));
        }
        String key = getKey(uid, StatisticTypeEnum.fromValue(type));
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
        return (T) getGoldStatistic(typeEnum, date, redisMap);
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
    public GoldStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getGoldStatistic(typeEnum, date, redisMap);
    }

    private GoldStatistic getGoldStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        Map<WayEnum, Integer> todayMap = new HashMap<>(16);
        Map<WayEnum, Integer> totalMap = new HashMap<>(16);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer todayNum = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer totalNum = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        redisMap.remove(dateNumStr);
        redisMap.remove(TOTAL);
        Set<String> keySet = redisMap.keySet();
        String str = date + UNDERLINE;
        for (String key : keySet) {
            // 判断是否是当天的数据
            if (key.startsWith(str)) {
                WayEnum wayEnum = WayEnum.fromName(key.substring(9));
                todayMap.put(wayEnum, redisMap.get(key));
                continue;
            }
            // 不是当天数据的，判断是否有下划线
            if (!key.contains(UNDERLINE)) {
                WayEnum wayEnum = WayEnum.fromName(key);
                totalMap.put(wayEnum, redisMap.get(key));
            }
        }
        return new GoldStatistic(todayNum, totalNum, date, typeEnum.getValue(), todayMap, totalMap);
    }

    public void increment(long uid, StatisticTypeEnum typeEnum, int date, int addValue, WayEnum way) {
        if (addValue < 0) {
            throw new GodException("统计增加值为负数");
        }
        String key = getKey(uid, typeEnum);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, addValue);
        redisHashUtil.increment(key, TOTAL, addValue);
        redisHashUtil.increment(key, date + UNDERLINE + way.getName(), addValue);
        redisHashUtil.increment(key, way.getName(), addValue);
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
