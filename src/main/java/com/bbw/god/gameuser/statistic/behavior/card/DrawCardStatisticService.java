package com.bbw.god.gameuser.statistic.behavior.card;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 抽卡行为service
 * @date 2020/4/16 9:41
 */
@Service
public class DrawCardStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.CARD_DRAW;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof DrawCardStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        DrawCardStatistic drawCardStatistic = (DrawCardStatistic) statistic;
        Integer date = drawCardStatistic.getDate();
        Integer today = drawCardStatistic.getToday();
        Integer total = drawCardStatistic.getTotal();
        Map<String, Integer> map = new HashMap<>(4);
        map.put(date + UNDERLINE + NUM, today);
        map.put(TOTAL, total);
        map.put(GOLD, drawCardStatistic.getGold());
        map.put(WOOD, drawCardStatistic.getWood());
        map.put(WATER, drawCardStatistic.getWater());
        map.put(FIRE, drawCardStatistic.getFire());
        map.put(EARTH, drawCardStatistic.getEarth());
        map.put(WANWU, drawCardStatistic.getEarth());
        map.put(JUX, drawCardStatistic.getJux());
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
        return (T) getDrawCardStatistic(date, redisMap);
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
    public DrawCardStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getDrawCardStatistic(date, redisMap);
    }

    private DrawCardStatistic getDrawCardStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer gold = redisMap.get(GOLD) == null ? 0 : redisMap.get(GOLD);
        Integer wood = redisMap.get(WOOD) == null ? 0 : redisMap.get(WOOD);
        Integer water = redisMap.get(WATER) == null ? 0 : redisMap.get(WATER);
        Integer fire = redisMap.get(FIRE) == null ? 0 : redisMap.get(FIRE);
        Integer earth = redisMap.get(EARTH) == null ? 0 : redisMap.get(EARTH);
        Integer wanwu = redisMap.get(WANWU) == null ? 0 : redisMap.get(WANWU);
        Integer jux = redisMap.get(JUX) == null ? 0 : redisMap.get(JUX);
        return new DrawCardStatistic(today, total, gold, wood, water, fire, earth, wanwu, jux, date);
    }

    public void drawCard(long uid, int type, int times, int date) {
        if (times < 0) {
            throw new GodException("统计增加值为负数");
        }
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, times);
        redisHashUtil.increment(key, TOTAL, times);
        String pool = "";
        CardPoolEnum poolEnum = CardPoolEnum.fromValue(type);
        switch (poolEnum) {
            case GOLD_CP:
                pool = GOLD;
                break;
            case WOOD_CP:
                pool = WOOD;
                break;
            case WATER_CP:
                pool = WATER;
                break;
            case FIRE_CP:
                pool = FIRE;
                break;
            case EARTH_CP:
                pool = EARTH;
                break;
            case WANWU_CP:
                pool = WANWU;
                break;
            case JUX_CP:
                pool = JUX;
                break;
            default:
                return;
        }
        redisHashUtil.increment(key, pool, times);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13350);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        DrawCardStatistic statistic = new DrawCardStatistic(0, value, DateUtil.getTodayInt());
        statistic.setGold(value);
        toRedis(uid, statistic);
    }
}
