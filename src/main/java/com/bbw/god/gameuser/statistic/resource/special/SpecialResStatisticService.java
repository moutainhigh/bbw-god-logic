package com.bbw.god.gameuser.statistic.resource.special;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
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
 * @description 特产统计service
 * @date 2020/4/20 16:17
 */
@Service
public class SpecialResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserAchievementService userAchievementService;

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
        return AwardEnum.TC;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof SpecialStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        SpecialStatistic specialStatistic = (SpecialStatistic) statistic;
        Map<String, Integer> map = new HashMap<>(16);
        Integer todayNum = specialStatistic.getToday();
        Integer totalNum = specialStatistic.getTotal();
        int type = specialStatistic.getType();
        Integer date = specialStatistic.getDate();
        map.put(date + UNDERLINE + NUM, todayNum);
        map.put(TOTAL, totalNum);
        Map<WayEnum, Integer> todayMap = specialStatistic.getTodayMap();
        Set<WayEnum> todayKeySet = todayMap.keySet();
        for (WayEnum todayWay : todayKeySet) {
            map.put(date + UNDERLINE + todayWay.getName(), todayMap.get(todayWay));
        }
        Map<WayEnum, Integer> totalMap = specialStatistic.getTotalMap();
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
        return (T) getSpecialStatistic(typeEnum, date, redisMap);
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
    public SpecialStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getSpecialStatistic(typeEnum, date, redisMap);
    }

    private SpecialStatistic getSpecialStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
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
        return new SpecialStatistic(todayNum, totalNum, date, typeEnum.getValue(), todayMap, totalMap);
    }

    public void increment(long uid, StatisticTypeEnum typeEnum, int date, int addValue, WayEnum way) {
        if (addValue < 0) {
            throw new GodException("统计增加值为负数");
        }
        SpecialStatistic statistic = fromRedis(uid, typeEnum, date);
        statistic.increment(addValue, way);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13700);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        increment(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(), value, WayEnum.TYF);
    }
}
