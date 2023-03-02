package com.bbw.god.gameuser.statistic.behavior.fight;

import com.bbw.exception.CoderException;
import com.bbw.god.fight.FightTypeEnum;
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
 * @description 战斗统计service
 * @date 2020/4/22 9:39
 */
@Service
public class FightStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FIGHT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FightStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        FightStatistic fightStatistic = (FightStatistic) statistic;
        Integer date = fightStatistic.getDate();
        Integer today = fightStatistic.getToday();
        Integer total = fightStatistic.getTotal();
        Map<FightTypeEnum, Integer> fightMap = fightStatistic.getFightMap();
        Map<FightTypeEnum, Integer> winFightMap = fightStatistic.getWinFightMap();
        Map<FightTypeEnum, Integer> todayFightMap = fightStatistic.getTodayFightMap();
        Map<FightTypeEnum, Integer> todayWinFightMap = fightStatistic.getTodayWinFightMap();
        Map<String, Integer> map = new HashMap<>(5);
        map.put(date + UNDERLINE + NUM, today);
        map.put(TOTAL, total);
        Set<FightTypeEnum> fightKeySet = fightMap.keySet();
        for (FightTypeEnum fightTypeEnum : fightKeySet) {
            map.put(fightTypeEnum.getName(), fightMap.get(fightTypeEnum));
        }
        Set<FightTypeEnum> winKeySet = winFightMap.keySet();
        for (FightTypeEnum fightTypeEnum : winKeySet) {
            map.put(fightTypeEnum.getName() + UNDERLINE + WIN, winFightMap.get(fightTypeEnum));
        }
        Set<FightTypeEnum> todayFightKeySet = todayFightMap.keySet();
        for (FightTypeEnum fightTypeEnum : todayFightKeySet) {
            map.put(date + UNDERLINE + fightTypeEnum.getName(), todayFightMap.get(fightTypeEnum));
        }
        Set<FightTypeEnum> todayWinFightKeySet = todayWinFightMap.keySet();
        for (FightTypeEnum fightTypeEnum : todayWinFightKeySet) {
            map.put(date + UNDERLINE + fightTypeEnum.getName() + UNDERLINE + WIN, todayWinFightMap.get(fightTypeEnum));
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
        return (T) getFightStatistic(date, redisMap);
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
    public FightStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getFightStatistic(date, redisMap);
    }

    private FightStatistic getFightStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Map<FightTypeEnum, Integer> fightMap = new HashMap<>(16);
        Map<FightTypeEnum, Integer> todayFightMap = new HashMap<>(16);
        Map<FightTypeEnum, Integer> winFightMap = new HashMap<>(16);
        Map<FightTypeEnum, Integer> todayWinFightMap = new HashMap<>(16);
        Set<String> keySet = redisMap.keySet();
        for (String key : keySet) {
            String[] split = key.split(UNDERLINE);
            if (!key.contains(UNDERLINE) && !key.equals(TOTAL)) {
                FightTypeEnum fightTypeEnum = FightTypeEnum.fromName(key);
                fightMap.put(fightTypeEnum, redisMap.get(key));
                continue;
            }
            if (key.contains(String.valueOf(date)) && !key.contains(NUM)) {
                FightTypeEnum fightTypeEnum = FightTypeEnum.fromName(split[1]);
                if (key.contains(WIN)) {
                    todayWinFightMap.put(fightTypeEnum, redisMap.get(key));
                    continue;
                }
                todayFightMap.put(fightTypeEnum, redisMap.get(key));
                continue;
            }
            if (key.contains(WIN) && split.length == 2) {
                FightTypeEnum fightTypeEnum = FightTypeEnum.fromName(split[0]);
                winFightMap.put(fightTypeEnum, redisMap.get(key));
            }
        }
        return new FightStatistic(today, total, date, fightMap, winFightMap, todayFightMap, todayWinFightMap);
    }

    public void incFightStatistic(long uid, int date, FightTypeEnum fightTypeEnum, boolean isWin) {
        FightStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.increment(fightTypeEnum, isWin);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement yeguaiAchievement = userAchievementService.getUserAchievement(uid, 620);
        UserAchievement youguaiAchievement = userAchievementService.getUserAchievement(uid, 630);
        UserAchievement lianbinAchievement = userAchievementService.getUserAchievement(uid, 640);
        UserAchievement fstAchievement = userAchievementService.getUserAchievement(uid, 720);
        int yeguaiWin = yeguaiAchievement == null ? 0 : yeguaiAchievement.getValue();
        int youguaiWin = youguaiAchievement == null ? 0 : youguaiAchievement.getValue();
        int lianbinWin = lianbinAchievement == null ? 0 : lianbinAchievement.getValue();
        int fstWin = fstAchievement == null ? 0 : fstAchievement.getValue();
        FightStatistic statistic = new FightStatistic();
        Map<FightTypeEnum, Integer> fightMap = statistic.getFightMap();
        Map<FightTypeEnum, Integer> winFightMap = statistic.getWinFightMap();
        fightMap.put(FightTypeEnum.YG, yeguaiWin);
        fightMap.put(FightTypeEnum.HELP_YG, youguaiWin);
        fightMap.put(FightTypeEnum.TRAINING, lianbinWin);
        fightMap.put(FightTypeEnum.FST, fstWin);
        winFightMap.put(FightTypeEnum.YG, yeguaiWin);
        winFightMap.put(FightTypeEnum.HELP_YG, youguaiWin);
        winFightMap.put(FightTypeEnum.TRAINING, lianbinWin);
        winFightMap.put(FightTypeEnum.FST, fstWin);
        statistic.setFightMap(fightMap);
        statistic.setWinFightMap(winFightMap);
        Set<FightTypeEnum> keySet = fightMap.keySet();
        int sum = 0;
        for (FightTypeEnum fightTypeEnum : keySet) {
            sum += fightMap.get(fightTypeEnum);
        }
        statistic.setTotal(sum);
        toRedis(uid, statistic);
    }
}
