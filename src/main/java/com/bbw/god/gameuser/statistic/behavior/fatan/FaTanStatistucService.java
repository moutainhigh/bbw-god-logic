package com.bbw.god.gameuser.statistic.behavior.fatan;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 法坛行为统计service
 *
 * @author fzj
 * @date 2021/11/1 18:17
 */
@Service
public class FaTanStatistucService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FA_TAN;
    }

    /**
     * 将统计数据持久化到redis
     * @param uid       玩家id
     * @param statistic 统计对象
     * @param <T>
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FaTanStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        FaTanStatistic faTanStatistic = (FaTanStatistic) statistic;
        Integer date = faTanStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, faTanStatistic.getToday());
        map.put(TOTAL, faTanStatistic.getTotal());
        map.put(FaTanStatistic.ALL_FATAN_LV, faTanStatistic.getTotalFaTanLv());
        map.put(FaTanStatistic.UNLOCK_FATAN_NUM, faTanStatistic.getUnlockFaTanNum());
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 从redis读取数据并转成统计对象
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return
     */
    @Override
    public FaTanStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new FaTanStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new FaTanStatistic(date, redisMap);
    }

    /**
     * 法坛升级统计
     * @param uid
     */
    public void doFaTanUpStatistic(long uid){
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //法坛升级统计
        redisHashUtil.increment(key, FaTanStatistic.ALL_FATAN_LV, 1);
    }

    /**
     * 法坛解锁统计
     * @param uid
     */
    public void doUnlockFaTanStatistic(long uid){
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //法坛升级统计
        redisHashUtil.increment(key, FaTanStatistic.UNLOCK_FATAN_NUM, 1);
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
