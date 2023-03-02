package com.bbw.god.gameuser.statistic.behavior.login;

import com.bbw.common.DateUtil;
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
 * @description 登录统计service
 * @date 2020/4/21 14:59
 */
@Service
public class LoginStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.LOGIN;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof LoginStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        LoginStatistic loginStatistic = (LoginStatistic) statistic;
        Integer date = loginStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(5);
        map.put(date + UNDERLINE + NUM, loginStatistic.getToday());
        map.put(TOTAL, loginStatistic.getTotal());
        map.put(LOGIN_DAYS, loginStatistic.getLoginDays());
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
        return (T) getLoginStatistic(date, redisMap);
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
    public LoginStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getLoginStatistic(date, redisMap);
    }

    private LoginStatistic getLoginStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer loginDays = redisMap.get(LOGIN_DAYS) == null ? 0 : redisMap.get(LOGIN_DAYS);
        return new LoginStatistic(today, total, date, loginDays);
    }

    public void incLoginDays(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, LOGIN_DAYS, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void incLoginTimes(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, DateUtil.getTodayInt() + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
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
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 14270);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        toRedis(uid, new LoginStatistic(0, value, DateUtil.getTodayInt(), value));
    }
}
