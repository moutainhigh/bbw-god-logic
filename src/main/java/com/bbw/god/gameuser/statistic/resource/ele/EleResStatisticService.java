package com.bbw.god.gameuser.statistic.resource.ele;

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
 * @description 元素统计service
 * @date 2020/4/20 11:08
 */
@Service
public class EleResStatisticService extends ResourceStatisticService {
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
        return AwardEnum.YS;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof EleStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        EleStatistic eleStatistic = (EleStatistic) statistic;
        Integer date = eleStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, eleStatistic.getToday());
        map.put(TOTAL, eleStatistic.getTotal());
        map.put(GOLD_ELE, eleStatistic.getGoldEle());
        map.put(WOOD_ELE, eleStatistic.getWoodEle());
        map.put(WATER_ELE, eleStatistic.getWaterEle());
        map.put(FIRE_ELE, eleStatistic.getFireEle());
        map.put(EARTH_ELE, eleStatistic.getEarthEle());
        Map<WayEnum, Integer> totalMap = eleStatistic.getTotalMap();
        Set<WayEnum> wayEnums = totalMap.keySet();
        for (WayEnum way : wayEnums) {
            map.put(way.getName(), totalMap.get(way));
        }
        Map<WayEnum, Integer> todayMap = eleStatistic.getTodayMap();
        Set<WayEnum> todayWays = todayMap.keySet();
        for (WayEnum way : todayWays) {
            map.put(date + UNDERLINE + way.getName(), todayMap.get(way));
        }
        Map<WayEnum, Integer> goldMap = eleStatistic.getGoldMap();
        Set<WayEnum> goldKeySet = goldMap.keySet();
        for (WayEnum goldWayEnum : goldKeySet) {
            map.put(GOLD_ELE + UNDERLINE + goldWayEnum.getName(), goldMap.get(goldWayEnum));
        }
        Map<WayEnum, Integer> woodMap = eleStatistic.getWoodMap();
        Set<WayEnum> woodKeySet = woodMap.keySet();
        for (WayEnum woodWayEnum : woodKeySet) {
            map.put(WOOD_ELE + UNDERLINE + woodWayEnum.getName(), woodMap.get(woodWayEnum));
        }
        Map<WayEnum, Integer> waterMap = eleStatistic.getWaterMap();
        Set<WayEnum> waterKeySet = waterMap.keySet();
        for (WayEnum waterWayEnum : waterKeySet) {
            map.put(WATER_ELE + UNDERLINE + waterWayEnum.getName(), waterMap.get(waterWayEnum));
        }
        Map<WayEnum, Integer> fireMap = eleStatistic.getFireMap();
        Set<WayEnum> fireKeySet = fireMap.keySet();
        for (WayEnum fireWayEnum : fireKeySet) {
            map.put(FIRE_ELE + UNDERLINE + fireWayEnum.getName(), fireMap.get(fireWayEnum));
        }
        Map<WayEnum, Integer> earthMap = eleStatistic.getEarthMap();
        Set<WayEnum> earthKeySet = earthMap.keySet();
        for (WayEnum earthWayEnum : earthKeySet) {
            map.put(EARTH_ELE + UNDERLINE + earthWayEnum.getName(), earthMap.get(earthWayEnum));
        }
        String key = getKey(uid, StatisticTypeEnum.fromValue(eleStatistic.getType()));
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
        return (T) getEleStatistic(typeEnum, date, redisMap);
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
    public EleStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getEleStatistic(typeEnum, date, redisMap);
    }

    private EleStatistic getEleStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer todayNum = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer totalNum = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer goldEle = redisMap.get(GOLD_ELE) == null ? 0 : redisMap.get(GOLD_ELE);
        Integer woodEle = redisMap.get(WOOD_ELE) == null ? 0 : redisMap.get(WOOD_ELE);
        Integer waterEle = redisMap.get(WATER_ELE) == null ? 0 : redisMap.get(WATER_ELE);
        Integer fireEle = redisMap.get(FIRE_ELE) == null ? 0 : redisMap.get(FIRE_ELE);
        Integer earthEle = redisMap.get(EARTH_ELE) == null ? 0 : redisMap.get(EARTH_ELE);
        Map<WayEnum, Integer> totalMap = new HashMap<>(16);
        Map<WayEnum, Integer> todayMap = new HashMap<>(16);
        Map<WayEnum, Integer> goldMap = new HashMap<>(16);
        Map<WayEnum, Integer> woodMap = new HashMap<>(16);
        Map<WayEnum, Integer> waterMap = new HashMap<>(16);
        Map<WayEnum, Integer> fireMap = new HashMap<>(16);
        Map<WayEnum, Integer> earthMap = new HashMap<>(16);
        Set<String> keySet = redisMap.keySet();
        for (String key : keySet) {
            String[] split = key.split(UNDERLINE);
            if (key.contains("Ele") && key.contains(UNDERLINE)) {
                WayEnum way = WayEnum.fromName(split[1]);
                Integer val = redisMap.get(key);
                String str = split[0].substring(0, split[0].length() - 3);
                switch (str) {
                    case "gold":
                        goldMap.put(way, val);
                        break;
                    case "wood":
                        woodMap.put(way, val);
                        break;
                    case "water":
                        waterMap.put(way, val);
                        break;
                    case "fire":
                        fireMap.put(way, val);
                        break;
                    case "earth":
                        earthMap.put(way, val);
                        break;
                }
            }
            if (!key.contains(NUM) && !key.contains("Ele") && !key.equals(TOTAL)) {
                if (!key.contains(UNDERLINE)) {
                    WayEnum way = WayEnum.fromName(key);
                    totalMap.put(way, redisMap.get(key));
                    continue;
                }
                if (key.contains(String.valueOf(date))) {
                    WayEnum way = WayEnum.fromName(split[1]);
                    todayMap.put(way, redisMap.get(key));
                }
            }
        }
        return new EleStatistic(todayNum, totalNum, date, typeEnum.getValue(), goldEle, woodEle, waterEle, fireEle,
                earthEle, totalMap, todayMap, goldMap, woodMap, waterMap, fireMap, earthMap);
    }

    /**
     * 增加统计值
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @param gold     金属性元素增加值
     * @param wood     木属性元素增加值
     * @param water    水属性元素增加值
     * @param fire     火属性元素增加值
     * @param earth    土属性元素增加值
     * @param way      途径
     */
    public void increment(long uid, StatisticTypeEnum typeEnum, int date, int gold, int wood, int water, int fire,
                          int earth, WayEnum way) {
        if (gold < 0 || wood < 0 || water < 0 || fire < 0 || earth < 0) {
            throw new GodException("统计增加值为负数");
        }
        EleStatistic eleStatistic = fromRedis(uid, typeEnum, date);
        eleStatistic.increment(gold, wood, water, fire, earth, way);
        toRedis(uid, eleStatistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement achievement = userAchievementService.getUserAchievement(uid, 210);
        int total = achievement == null ? 0 : achievement.getValue();
        UserAchievement goldAchievement = userAchievementService.getUserAchievement(uid, 220);
        UserAchievement woodAchievement = userAchievementService.getUserAchievement(uid, 230);
        UserAchievement waterAchievement = userAchievementService.getUserAchievement(uid, 240);
        UserAchievement fireAchievement = userAchievementService.getUserAchievement(uid, 250);
        UserAchievement earthAchievement = userAchievementService.getUserAchievement(uid, 260);
        UserAchievement cunZAchievement = userAchievementService.getUserAchievement(uid, 13770);
        int gold = goldAchievement == null ? 0 : goldAchievement.getValue();
        int wood = woodAchievement == null ? 0 : woodAchievement.getValue();
        int water = waterAchievement == null ? 0 : waterAchievement.getValue();
        int fire = fireAchievement == null ? 0 : fireAchievement.getValue();
        int earth = earthAchievement == null ? 0 : earthAchievement.getValue();
        toRedis(uid, new EleStatistic(total, total, DateUtil.getTodayInt(), StatisticTypeEnum.CONSUME.getValue(), gold,
                wood, water, fire, earth));
        int cunZ = cunZAchievement == null ? 0 : cunZAchievement.getValue();
        increment(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt(), cunZ, 0, 0, 0, 0, WayEnum.CZ);
    }
}
