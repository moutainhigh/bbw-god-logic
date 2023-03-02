package com.bbw.god.gameuser.statistic.behavior.randomevent;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 随机事件统计service
 * @date 2020/4/22 16:11
 */
@Service
public class RandomEventStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.RANDOM_EVENT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof RandomEventStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        RandomEventStatistic randomEventStatistic = (RandomEventStatistic) statistic;
        Integer date = randomEventStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, randomEventStatistic.getToday());
        map.put(TOTAL, randomEventStatistic.getTotal());
        map.put(CONTINUOUS_DEBUFF, randomEventStatistic.getContinuousDebuff());
        Map<YdEventEnum, Integer> todayMap = randomEventStatistic.getTodayMap();
        Map<YdEventEnum, Integer> totalMap = randomEventStatistic.getTotalMap();
        Set<YdEventEnum> todayKeySet = todayMap.keySet();
        for (YdEventEnum todayKey : todayKeySet) {
            if (null == todayKey) {
                logger.error("野地随机事件统计枚举类为null");
                continue;
            }
            map.put(date + UNDERLINE + todayKey.getName(), todayMap.get(todayKey));
        }
        Set<YdEventEnum> totalKeySet = totalMap.keySet();
        for (YdEventEnum totalKey : totalKeySet) {
            // 避免未知的key转成空的枚举引发的空指针问题
            if (null == totalKey) {
                continue;
            }
            map.put(totalKey.getName(), totalMap.get(totalKey));
        }
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
        return (T) getRandomEventStatistic(date, redisMap);
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
    public RandomEventStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getRandomEventStatistic(date, redisMap);
    }

    private RandomEventStatistic getRandomEventStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer continuousDebuff = redisMap.get(CONTINUOUS_DEBUFF) == null ? 0 : redisMap.get(CONTINUOUS_DEBUFF);
        redisMap.remove(TOTAL);
        redisMap.remove(CONTINUOUS_DEBUFF);
        Map<YdEventEnum, Integer> todayMap = new HashMap<>(16);
        Map<YdEventEnum, Integer> totalMap = new HashMap<>(16);
        Set<String> keySet = redisMap.keySet();
        String str = date + UNDERLINE;
        for (String key : keySet) {
            if (key.startsWith(str) && !key.contains(NUM)) {
                String[] split = key.split(UNDERLINE);
                YdEventEnum ydEventEnum = YdEventEnum.fromName(split[1]);
                todayMap.put(ydEventEnum, redisMap.get(key));
                continue;
            }
            if (!key.contains(UNDERLINE)) {
                YdEventEnum ydEventEnum = YdEventEnum.fromName(key);
                totalMap.put(ydEventEnum, redisMap.get(key));
            }
        }
        return new RandomEventStatistic(today, total, date, todayMap, totalMap, continuousDebuff);
    }

    public void meetRandomEvent(long uid, int date, YdEventEnum ydEventEnum) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + ydEventEnum.getName(), 1);
        redisHashUtil.increment(key, ydEventEnum.getName(), 1);
        if (YdEventEnum.getDebuffEventList().contains(ydEventEnum)) {
            redisHashUtil.increment(key, CONTINUOUS_DEBUFF, 1);
        } else {
            redisHashUtil.putField(key, CONTINUOUS_DEBUFF, 0);
        }
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
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13730);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, YdEventEnum.QIANG_DAO.getName(), value);
        UserAchievement debuffAchievement = userAchievementService.getUserAchievement(uid, 14150);
        int debuffValue = debuffAchievement == null ? 0 : debuffAchievement.getValue();
        redisHashUtil.increment(key, CONTINUOUS_DEBUFF, debuffValue);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }
}
