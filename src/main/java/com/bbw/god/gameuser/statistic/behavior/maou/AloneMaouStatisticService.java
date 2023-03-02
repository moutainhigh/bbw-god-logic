package com.bbw.god.gameuser.statistic.behavior.maou;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.TypeEnum;
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

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 独战魔王统计service
 * @date 2020/4/22 10:35
 */
@Service
public class AloneMaouStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.MAOU_ALONE_FIGHT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof AloneMaouStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        AloneMaouStatistic aloneMaouStatistic = (AloneMaouStatistic) statistic;
        Integer date = aloneMaouStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, aloneMaouStatistic.getToday());
        map.put(TOTAL, aloneMaouStatistic.getTotal());
        map.put(GOLD_ALONE_MAOU, aloneMaouStatistic.getGold());
        map.put(WOOD_ALONE_MAOU, aloneMaouStatistic.getWood());
        map.put(WATER_ALONE_MAOU, aloneMaouStatistic.getWater());
        map.put(FIRE_ALONE_MAOU, aloneMaouStatistic.getFire());
        map.put(EARTH_ALONE_MAOU, aloneMaouStatistic.getEarth());
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
        return (T) getAloneMaouStatistic(date, redisMap);
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
    public AloneMaouStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getAloneMaouStatistic(date, redisMap);
    }

    private AloneMaouStatistic getAloneMaouStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer gold = redisMap.get(GOLD_ALONE_MAOU) == null ? 0 : redisMap.get(GOLD_ALONE_MAOU);
        Integer wood = redisMap.get(WOOD_ALONE_MAOU) == null ? 0 : redisMap.get(WOOD_ALONE_MAOU);
        Integer water = redisMap.get(WATER_ALONE_MAOU) == null ? 0 : redisMap.get(WATER_ALONE_MAOU);
        Integer fire = redisMap.get(FIRE_ALONE_MAOU) == null ? 0 : redisMap.get(FIRE_ALONE_MAOU);
        Integer earth = redisMap.get(EARTH_ALONE_MAOU) == null ? 0 : redisMap.get(EARTH_ALONE_MAOU);
        return new AloneMaouStatistic(today, total, date, gold, wood, water, fire, earth);
    }

    public void killAloneMaou(long uid, int maouLevel, int maouType) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        TypeEnum typeEnum = TypeEnum.fromValue(maouType);
        String field;
        switch (typeEnum) {
            case Gold:
                field = GOLD_ALONE_MAOU;
                break;
            case Wood:
                field = WOOD_ALONE_MAOU;
                break;
            case Water:
                field = WATER_ALONE_MAOU;
                break;
            case Fire:
                field = FIRE_ALONE_MAOU;
                break;
            case Earth:
                field = EARTH_ALONE_MAOU;
                break;
            default:
                return;
        }
        Integer value = redisHashUtil.getField(key, field) == null ? 0 : redisHashUtil.getField(key, field);
        if (maouLevel > value) {
            redisHashUtil.putField(key, field, maouLevel);
        }
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, DateUtil.getTodayInt() + UNDERLINE + NUM, 1);
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
        UserAchievement goldAchievement = userAchievementService.getUserAchievement(uid, 850);
        UserAchievement woodAchievement = userAchievementService.getUserAchievement(uid, 860);
        UserAchievement waterAchievement = userAchievementService.getUserAchievement(uid, 870);
        UserAchievement fireAchievement = userAchievementService.getUserAchievement(uid, 880);
        UserAchievement earthAchievement = userAchievementService.getUserAchievement(uid, 890);
        int gold = goldAchievement == null ? 0 : goldAchievement.getValue();
        int wood = woodAchievement == null ? 0 : woodAchievement.getValue();
        int water = waterAchievement == null ? 0 : waterAchievement.getValue();
        int fire = fireAchievement == null ? 0 : fireAchievement.getValue();
        int earth = earthAchievement == null ? 0 : earthAchievement.getValue();
        int total = gold + wood + water + fire + earth;
        toRedis(uid, new AloneMaouStatistic(0, total, DateUtil.getTodayInt(), gold, wood, water, fire, earth));
    }
}
