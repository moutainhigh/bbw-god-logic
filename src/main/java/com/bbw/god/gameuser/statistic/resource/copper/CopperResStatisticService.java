package com.bbw.god.gameuser.statistic.resource.copper;

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
 * @description 铜钱统计service
 * @date 2020/4/20 14:14
 */
@Service
public class CopperResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Long> redisHashUtil;
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
        return AwardEnum.TQ;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof CopperStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        CopperStatistic copperStatistic = (CopperStatistic) statistic;
        Map<String, Long> map = new HashMap<>(16);
        Long todayNum = copperStatistic.getTodayNum();
        Long totalNum = copperStatistic.getTotalNum();
        int type = copperStatistic.getType();
        Integer date = copperStatistic.getDate();
        map.put(date + UNDERLINE + NUM, todayNum);
        map.put(TOTAL, totalNum);
        map.put(date + UNDERLINE + COPPER_PROFIT, copperStatistic.getTodayProfit());
        map.put(COPPER_PROFIT, copperStatistic.getTotalProfit());
        Map<WayEnum, Long> todayMap = copperStatistic.getTodayMap();
        Set<WayEnum> todayKeySet = todayMap.keySet();
        for (WayEnum todayWay : todayKeySet) {
            map.put(date + UNDERLINE + todayWay.getName(), todayMap.get(todayWay));
        }
        Map<WayEnum, Long> totalMap = copperStatistic.getTotalMap();
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
        Map<String, Long> redisMap = new HashMap<>();
        for (String mapKey : map.keySet()) {
            redisMap.put(mapKey, Long.class.cast(map.get(mapKey)));
        }
        return (T) getCopperStatistic(typeEnum, date, redisMap);
    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid  玩家id
     * @param type 类型枚举
     * @param date 日期
     * @return 统计对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public CopperStatistic fromRedis(long uid, StatisticTypeEnum type, int date) {
        Map<String, Long> redisMap = redisHashUtil.get(getKey(uid, type));
        return getCopperStatistic(type, date, redisMap);
    }

    private CopperStatistic getCopperStatistic(StatisticTypeEnum type, int date, Map<String, Long> redisMap) {
        Map<WayEnum, Long> todayMap = new HashMap<>(16);
        Map<WayEnum, Long> totalMap = new HashMap<>(16);
        String dateNumStr = date + UNDERLINE + NUM;
        String dateProfitStr = date + UNDERLINE + COPPER_PROFIT;
        Long todayNum = redisMap.get(dateNumStr) == null ? 0L : redisMap.get(dateNumStr);
        Long totalNum = redisMap.get(TOTAL) == null ? 0L : redisMap.get(TOTAL);
        Long todayLose = redisMap.get(dateProfitStr) == null ? 0L : redisMap.get(dateProfitStr);
        Long totalLose = redisMap.get(COPPER_PROFIT) == null ? 0L : redisMap.get(COPPER_PROFIT);
        redisMap.remove(dateNumStr);
        redisMap.remove(TOTAL);
        redisMap.remove(dateProfitStr);
        redisMap.remove(COPPER_PROFIT);
        Set<String> keySet = redisMap.keySet();
        String str = date + UNDERLINE;
        for (String key : keySet) {
            // 判断是否是当天的数据
            if (key.startsWith(str) && !key.contains(COPPER_PROFIT)) {
                WayEnum wayEnum = WayEnum.fromName(key.substring(9));
                todayMap.put(wayEnum, redisMap.get(key));
                continue;
            }
            // 不是当天数据的，判断是否有下划线
            if (!key.contains(UNDERLINE) && !key.contains(COPPER_PROFIT) && !key.equals(TOTAL)) {
                WayEnum wayEnum = WayEnum.fromName(key);
                totalMap.put(wayEnum, redisMap.get(key));
            }
        }
        return new CopperStatistic(date, type.getValue(), todayNum, totalNum, todayLose, totalLose, todayMap, totalMap);
    }

    /**
     * 获得铜钱
     *
     * @param uid       玩家id
     * @param date      日期
     * @param addCopper 增加值
     * @param profit    利润
     * @param way       途径
     */
    public void addCopper(long uid, int date, long addCopper, long profit, WayEnum way) {
        if (addCopper < 0 || profit < 0) {
            throw new GodException("统计增加值为负数");
        }
        CopperStatistic statistic = fromRedis(uid, StatisticTypeEnum.GAIN, date);
        statistic.addCopper(addCopper, profit, way);
        toRedis(uid, statistic);
    }

    /**
     * 消耗铜钱
     *
     * @param uid          玩家id
     * @param date         日期
     * @param deductCopper 消耗铜钱数
     * @param way          途径
     */
    public void deductCopper(long uid, int date, long deductCopper, WayEnum way) {
        if (deductCopper < 0) {
            throw new GodException("统计增加值为负数");
        }
        CopperStatistic statistic = fromRedis(uid, StatisticTypeEnum.CONSUME, date);
        statistic.addCopper(deductCopper, 0, way);
        toRedis(uid, statistic);
    }

    /**
     * 增加铜钱收益统计
     *
     * @param uid       玩家id
     * @param date      日期
     * @param addProfit 增加值
     */
    public void incProfitStatistic(long uid, StatisticTypeEnum typeEnum, int date, int addProfit) {
        if (addProfit < 0) {
            throw new GodException("统计增加值为负数");
        }
        CopperStatistic statistic = fromRedis(uid, typeEnum, date);
        statistic.addProfit(addProfit);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 14320);
        long value = userAchievement == null ? 0 : userAchievement.getValue();
        UserAchievement achievement = userAchievementService.getUserAchievement(uid, 14110);
        long deProfit = achievement == null ? 0 : achievement.getValue();
        UserAchievement jiebeiAchievement = userAchievementService.getUserAchievement(uid, 13740);
        long jiebei = jiebeiAchievement == null ? 0 : jiebeiAchievement.getValue();
        CopperStatistic statistic = new CopperStatistic(StatisticTypeEnum.GAIN.getValue(), value, value, value, value);
        Map<WayEnum, Long> totalMap = statistic.getTotalMap();
        totalMap.put(WayEnum.JB, jiebei * 2000);
        totalMap.put(WayEnum.TRADE, value);
        statistic.setTotalMap(totalMap);
        statistic.setTodayMap(totalMap);
        toRedis(uid, statistic);
        CopperStatistic copperStatistic = new CopperStatistic(StatisticTypeEnum.CONSUME.getValue(), deProfit, deProfit,
                deProfit, deProfit);
        Map<WayEnum, Long> map = copperStatistic.getTotalMap();
        map.put(WayEnum.TRADE, deProfit);
        copperStatistic.setTotalMap(map);
        copperStatistic.setTodayMap(map);
        toRedis(uid, copperStatistic);
    }
}
