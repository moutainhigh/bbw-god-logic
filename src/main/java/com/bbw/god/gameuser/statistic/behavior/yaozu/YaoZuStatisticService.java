package com.bbw.god.gameuser.statistic.behavior.yaozu;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.task.CunZTaskStatistic;
import com.bbw.god.gameuser.yaozu.YaoZuEnum;
import com.bbw.god.gameuser.yaozu.YaoZuTool;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 妖族行为统计service
 *
 * @author fzj
 * @date 2021/9/8 15:52
 */
@Service
public class YaoZuStatisticService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.YAO_ZU_WIN;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof YaoZuStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        YaoZuStatistic yaoZuStatistic = (YaoZuStatistic) statistic;
        Integer date = yaoZuStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, yaoZuStatistic.getToday());
        map.put(TOTAL, yaoZuStatistic.getTotal());
        Map<String, Integer> beatYaoZuNums = yaoZuStatistic.getBeatYaoZuNums();
        if (null != beatYaoZuNums) {
            for (String typeField : beatYaoZuNums.keySet()) {
                map.put(typeField, beatYaoZuNums.get(typeField));
            }
        }
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
     * @return 统计对象
     */
    @Override
    public YaoZuStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new YaoZuStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new YaoZuStatistic(date, redisMap);
    }

    /**
     * 执行统计
     * @param uid
     * @param yaoZuId 妖族id
     */
    public void doStatistic (long uid, int yaoZuId){
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //击败统计
        Integer yaoZuType = YaoZuTool.getYaoZu(yaoZuId).getYaoZuType();
        redisHashUtil.increment(key, YaoZuEnum.fromValue(yaoZuType).getName(), 1);
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
