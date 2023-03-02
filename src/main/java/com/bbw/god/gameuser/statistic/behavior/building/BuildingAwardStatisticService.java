package com.bbw.god.gameuser.statistic.behavior.building;

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
 * @author suchaobin
 * @description 领取城内建筑物产出奖励统计service
 * @date 2020/11/24 17:00
 */
@Service
public class BuildingAwardStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BUILDING_AWARD;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof BuildingAwardStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        BuildingAwardStatistic buildingAwardStatistic = (BuildingAwardStatistic) statistic;
        Integer date = buildingAwardStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(4);
        map.put(date + UNDERLINE + NUM, buildingAwardStatistic.getToday());
        map.put(TOTAL, buildingAwardStatistic.getTotal());
        map.put(JXZ, buildingAwardStatistic.getJxzTotal());
        map.put(QZ, buildingAwardStatistic.getQzTotal());
        map.put(KC, buildingAwardStatistic.getKcTotal());
        map.put(LBL, buildingAwardStatistic.getLblTotal());
        map.put(LDF, buildingAwardStatistic.getLdfTotal());
        map.put(date + UNDERLINE + JXZ, buildingAwardStatistic.getJxzToday());
        map.put(date + UNDERLINE + QZ, buildingAwardStatistic.getQzToday());
        map.put(date + UNDERLINE + KC, buildingAwardStatistic.getKcToday());
        map.put(date + UNDERLINE + LBL, buildingAwardStatistic.getLblToday());
        map.put(date + UNDERLINE + LDF, buildingAwardStatistic.getLdfTotal());

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
        return (T) getBuildingAwardStatistic(date, redisMap);
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
    public BuildingAwardStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getBuildingAwardStatistic(date, redisMap);
    }

    private BuildingAwardStatistic getBuildingAwardStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer jxz = redisMap.get(JXZ) == null ? 0 : redisMap.get(JXZ);
        Integer qz = redisMap.get(QZ) == null ? 0 : redisMap.get(QZ);
        Integer kc = redisMap.get(KC) == null ? 0 : redisMap.get(KC);
        Integer lbl = redisMap.get(LBL) == null ? 0 : redisMap.get(LBL);
        Integer ldf = redisMap.get(LDF) == null ? 0 : redisMap.get(LDF);
        Integer jxzToday = redisMap.get(date + UNDERLINE + JXZ) == null ? 0 : redisMap.get(date + UNDERLINE + JXZ);
        Integer qzToday = redisMap.get(date + UNDERLINE + QZ) == null ? 0 : redisMap.get(date + UNDERLINE + QZ);
        Integer kcToday = redisMap.get(date + UNDERLINE + KC) == null ? 0 : redisMap.get(date + UNDERLINE + KC);
        Integer lblToday = redisMap.get(date + UNDERLINE + LBL) == null ? 0 : redisMap.get(date + UNDERLINE + LBL);
        Integer ldfToday = redisMap.get(date + UNDERLINE + LDF) == null ? 0 : redisMap.get(date + UNDERLINE + LDF);
        return new BuildingAwardStatistic(today, total, date, jxz, qz, kc, lbl, ldf, jxzToday, qzToday, kcToday, lblToday, ldfToday);
    }

    public void addJxz(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, JXZ, 1);
        redisHashUtil.increment(key, date + UNDERLINE + JXZ, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void addQz(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, QZ, 1);
        redisHashUtil.increment(key, date + UNDERLINE + QZ, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void addKc(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, KC, 1);
        redisHashUtil.increment(key, date + UNDERLINE + KC, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void addLbl(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, LBL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + LBL, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void addLdf(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, LDF, 1);
        redisHashUtil.increment(key, date + UNDERLINE + LDF, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
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
