package com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram;

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
 * 文王64卦service
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:30
 */
@Service
public class HexagramStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.WWM_HEXAGRAM;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof HexagramStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        HexagramStatistic hexagramStatistic = (HexagramStatistic) statistic;
        Integer date = hexagramStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, hexagramStatistic.getToday());
        map.put(TOTAL, hexagramStatistic.getTotal());
        map.put(HEXAGRAM + UNDERLINE + NUM, hexagramStatistic.getHexagramNum());
        map.put(HEXAGRAM_UP_UP + UNDERLINE + NUM, hexagramStatistic.getHexagramUpUpNum());
        map.put(HEXAGRAM_DOWN_DOWN + UNDERLINE + NUM, hexagramStatistic.getHexagramDownDownNum());
        map.put(HEXAGRAM_CONTINUOUS_UP_UP, hexagramStatistic.getContinuousUpUp());
        map.put(HEXAGRAM_CONTINUOUS_DOWN_DOWN, hexagramStatistic.getContinuousDownDown());
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
        return (T) getHexagramStatistic(date, redisMap);
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
    public HexagramStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getHexagramStatistic(date, redisMap);
    }

    private HexagramStatistic getHexagramStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer hexagramNum = redisMap.get(HEXAGRAM + UNDERLINE + NUM) == null ? 0 : redisMap.get(HEXAGRAM + UNDERLINE + NUM);
        Integer hexagramUpUpNum = redisMap.get(HEXAGRAM_UP_UP + UNDERLINE + NUM) == null ? 0 : redisMap.get(HEXAGRAM_UP_UP + UNDERLINE + NUM);
        Integer hexagramDownDownNum = redisMap.get(HEXAGRAM_DOWN_DOWN + UNDERLINE + NUM) == null ? 0 : redisMap.get(HEXAGRAM_DOWN_DOWN + UNDERLINE + NUM);
        Integer continuousUpUp = redisMap.get(HEXAGRAM_CONTINUOUS_UP_UP) == null ? 0 : redisMap.get(HEXAGRAM_CONTINUOUS_UP_UP);
        Integer continuousDownDown = redisMap.get(HEXAGRAM_CONTINUOUS_DOWN_DOWN) == null ? 0 : redisMap.get(HEXAGRAM_CONTINUOUS_DOWN_DOWN);
        return new HexagramStatistic(today, total, date, hexagramNum, hexagramUpUpNum, hexagramDownDownNum, continuousUpUp, continuousDownDown);
    }

    public void draw(long uid, int date, boolean isAddHexagramNum, boolean isUpUp, boolean isDownDown) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        if (isAddHexagramNum) {
            redisHashUtil.increment(key, HEXAGRAM + UNDERLINE + NUM, 1);
        }
        if (isAddHexagramNum && isUpUp) {
            redisHashUtil.increment(key, HEXAGRAM_UP_UP + UNDERLINE + NUM, 1);
        }
        if (isAddHexagramNum && isDownDown) {
            redisHashUtil.increment(key, HEXAGRAM_DOWN_DOWN + UNDERLINE + NUM, 1);
        }
        if (isUpUp) {
            redisHashUtil.increment(key, HEXAGRAM_CONTINUOUS_UP_UP, 1);
        } else {
            redisHashUtil.putField(key, HEXAGRAM_CONTINUOUS_UP_UP, 0);
        }
        if (isDownDown) {
            redisHashUtil.increment(key, HEXAGRAM_CONTINUOUS_DOWN_DOWN, 1);
        } else {
            redisHashUtil.putField(key, HEXAGRAM_CONTINUOUS_DOWN_DOWN, 0);
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

    }
}
