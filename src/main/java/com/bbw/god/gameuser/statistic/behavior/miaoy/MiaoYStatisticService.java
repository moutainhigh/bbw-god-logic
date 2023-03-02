package com.bbw.god.gameuser.statistic.behavior.miaoy;

import com.bbw.exception.CoderException;
import com.bbw.god.city.miaoy.DrawResult;
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
 * @description 文王庙统计service
 * @date 2020/4/23 11:30
 */
@Service
public class MiaoYStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.WWM_DRAW;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof MiaoYStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        MiaoYStatistic miaoYStatistic = (MiaoYStatistic) statistic;
        Integer date = miaoYStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, miaoYStatistic.getToday());
        map.put(TOTAL, miaoYStatistic.getTotal());
        map.put(DrawResult.UP_UP.getName(), miaoYStatistic.getUpUp());
        map.put(DrawResult.UP.getName(), miaoYStatistic.getUp());
        map.put(DrawResult.MIDDLE.getName(), miaoYStatistic.getMiddle());
        map.put(DrawResult.DOWN.getName(), miaoYStatistic.getDown());
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
        return (T) getMiaoYStatistic(date, redisMap);
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
    public MiaoYStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getMiaoYStatistic(date, redisMap);
    }

    private MiaoYStatistic getMiaoYStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer upUp = redisMap.get(DrawResult.UP_UP.getName()) == null ? 0 : redisMap.get(DrawResult.UP_UP.getName());
        Integer up = redisMap.get(DrawResult.UP.getName()) == null ? 0 : redisMap.get(DrawResult.UP.getName());
        Integer middle = redisMap.get(DrawResult.MIDDLE.getName()) == null ?
                0 : redisMap.get(DrawResult.MIDDLE.getName());
        Integer down = redisMap.get(DrawResult.DOWN.getName()) == null ? 0 : redisMap.get(DrawResult.DOWN.getName());
        return new MiaoYStatistic(today, total, date, upUp, up, middle, down);
    }

    public void draw(long uid, int date, DrawResult drawResult) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, drawResult.getName(), 1);
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
        MiaoYStatistic statistic = new MiaoYStatistic();
        UserAchievement achievement = userAchievementService.getUserAchievement(uid, 13870);
        int value = achievement == null ? 0 : achievement.getValue();
        statistic.setUpUp(value);
        statistic.setTotal(value);
        toRedis(uid, statistic);
    }
}
