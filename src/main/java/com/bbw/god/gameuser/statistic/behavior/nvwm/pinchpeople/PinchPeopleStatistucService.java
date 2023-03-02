package com.bbw.god.gameuser.statistic.behavior.nvwm.pinchpeople;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 捏人行为统计service
 *
 * @author: huanghb
 * @date: 2022/5/20 17:26
 */
@Service
public class PinchPeopleStatistucService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.PINCH_PEOPLE;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     * @param <T>
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof PinchPeopleStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        PinchPeopleStatistic pinchPeopleStatistic = (PinchPeopleStatistic) statistic;
        Integer date = pinchPeopleStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, pinchPeopleStatistic.getToday());
        map.put(TOTAL, pinchPeopleStatistic.getTotal());
        map.put(PinchPeopleStatistic.PINCH_PEOPLE_TIMES, pinchPeopleStatistic.getPinchPeoleTimes());
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return
     */
    @Override
    public PinchPeopleStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new PinchPeopleStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new PinchPeopleStatistic(date, redisMap);
    }

    /**
     * 捏人统计
     *
     * @param uid
     */
    public void doPinchPeopleStatistic(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //法坛升级统计
        redisHashUtil.increment(key, PinchPeopleStatistic.PINCH_PEOPLE_TIMES, 1);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {

    }
}
