package com.bbw.god.gameuser.statistic.behavior.box;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.CfgTaskConfig;
import com.bbw.god.gameuser.task.TaskTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 开启每日任务宝箱service
 * @date 2020/4/21 14:03
 */
@Service
public class OpenDailyTaskBoxStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.OPEN_DAILY_TASK_BOX;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof OpenDailyTaskBoxStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        OpenDailyTaskBoxStatistic openDailyTaskBoxStatistic = (OpenDailyTaskBoxStatistic) statistic;
        Integer date = openDailyTaskBoxStatistic.getDate();
        Integer today = openDailyTaskBoxStatistic.getToday();
        Integer total = openDailyTaskBoxStatistic.getTotal();
        Integer openAllTodayBox = openDailyTaskBoxStatistic.getOpenAllTodayBox();
        Map<String, Integer> map = new HashMap<>(5);
        map.put(date + UNDERLINE + NUM, today);
        map.put(TOTAL, total);
        map.put(OPEN_ALL_TODAY_BOX, openAllTodayBox);
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
        return (T) getOpenDailyTaskBoxStatistic(date, redisMap);
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
    public OpenDailyTaskBoxStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getOpenDailyTaskBoxStatistic(date, redisMap);
    }

    private OpenDailyTaskBoxStatistic getOpenDailyTaskBoxStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer openAllTodayBox = redisMap.get(OPEN_ALL_TODAY_BOX) == null ? 0 : redisMap.get(OPEN_ALL_TODAY_BOX);
        return new OpenDailyTaskBoxStatistic(today, total, date, openAllTodayBox);
    }

    public void openBox(long uid, int date, int boxId, int score) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        int groupId = boxId / 1000 * 1000;
        CfgTaskConfig cfgTaskConfig = TaskTool.getTaskConfig(groupId);
        CfgTaskConfig.CfgBox cfgBox = cfgTaskConfig.getBoxs().stream().max(Comparator.comparingInt(
                CfgTaskConfig.CfgBox::getScore)).orElse(null);
        if (cfgBox != null && score == cfgBox.getScore()) {
            redisHashUtil.increment(key, OPEN_ALL_TODAY_BOX, 1);
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
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13140);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        OpenDailyTaskBoxStatistic statistic = new OpenDailyTaskBoxStatistic();
        statistic.setOpenAllTodayBox(value);
        statistic.setTotal(value * 5);
        toRedis(uid, statistic);
    }
}
