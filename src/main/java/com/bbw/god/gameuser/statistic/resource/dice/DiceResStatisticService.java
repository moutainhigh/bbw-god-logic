package com.bbw.god.gameuser.statistic.resource.dice;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 体力统计service
 * @date 2020/11/05 11:14
 */
@Service
public class DiceResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;

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
        return AwardEnum.TL;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof DiceStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        DiceStatistic diceStatistic = (DiceStatistic) statistic;
        Map<String, Integer> map = new HashMap<>(16);
        Integer todayNum = diceStatistic.getToday();
        Integer totalNum = diceStatistic.getTotal();
        int type = diceStatistic.getType();
        Integer date = diceStatistic.getDate();
        map.put(date + UNDERLINE + NUM, todayNum);
        map.put(TOTAL, totalNum);
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
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) getDiceStatistic(typeEnum, date, redisMap);
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
    public DiceStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getDiceStatistic(typeEnum, date, redisMap);
    }

    private DiceStatistic getDiceStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer todayNum = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer totalNum = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        return new DiceStatistic(todayNum, totalNum, date, typeEnum.getValue());
    }

    public void increment(long uid, StatisticTypeEnum typeEnum, int date, int addValue) {
        if (addValue < 0) {
            throw new GodException("统计增加值为负数");
        }
        String key = getKey(uid, typeEnum);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, addValue);
        redisHashUtil.increment(key, TOTAL, addValue);
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
