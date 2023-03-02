package com.bbw.god.gameuser.statistic.behavior.nvwm;

import com.bbw.exception.CoderException;
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
 * @description 女娲庙捐赠统计service
 * @date 2020/4/23 9:02
 */
@Service
public class NvwmStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.NVWM_DONATE;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof NvwmStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        NvwmStatistic nvwmStatistic = (NvwmStatistic) statistic;
        Integer date = nvwmStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(7);
        map.put(date + UNDERLINE + NUM, nvwmStatistic.getToday());
        map.put(TOTAL, nvwmStatistic.getTotal());
        map.put(date + UNDERLINE + FAVORITE, nvwmStatistic.getTodayFavorite());
        map.put(FAVORITE, nvwmStatistic.getTotalFavorite());
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
        return (T) getNvwmStatistic(date, redisMap);
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
    public NvwmStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getNvwmStatistic(date, redisMap);
    }

    private NvwmStatistic getNvwmStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        String dateFavoriteStr = date + UNDERLINE + FAVORITE;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer todayFavorite = redisMap.get(dateFavoriteStr) == null ?
                0 : redisMap.get(dateFavoriteStr);
        Integer totalFavorite = redisMap.get(FAVORITE) == null ? 0 : redisMap.get(FAVORITE);
        return new NvwmStatistic(today, total, date, todayFavorite, totalFavorite);
    }

    public void donate(long uid, int date, int addFavorite) {
        String key = getKey(uid, null);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + FAVORITE, addFavorite);
        redisHashUtil.increment(key, FAVORITE, addFavorite);
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
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13710);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        NvwmStatistic statistic = new NvwmStatistic();
        statistic.setTodayFavorite(value);
        statistic.setTotalFavorite(value);
        toRedis(uid, statistic);
    }
}
