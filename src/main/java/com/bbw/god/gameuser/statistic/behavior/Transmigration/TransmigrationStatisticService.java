package com.bbw.god.gameuser.statistic.behavior.Transmigration;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.event.EPTransmigrationSuccess;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 轮回世界统计服务
 *
 * @author fzj
 * @date 2021/9/17 17:25
 */
@Service
public class TransmigrationStatisticService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.TRANSMIGRATION_CHALLENGE;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof TransmigrationStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        TransmigrationStatistic transmigrationStatistic = (TransmigrationStatistic) statistic;
        Integer date = transmigrationStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, transmigrationStatistic.getToday());
        map.put(TOTAL, transmigrationStatistic.getTotal());
        Map<String, Integer> successNumPerTransmigration = transmigrationStatistic.getSuccessNumPerTransmigration();
        if (null != successNumPerTransmigration) {
            for (String transmigration : successNumPerTransmigration.keySet()) {
                map.put(transmigration, successNumPerTransmigration.get(transmigration));
            }
        }
        Map<String, Integer> successPerCity = transmigrationStatistic.getSuccessPerCity();
        if (null != successPerCity) {
            for (String typeField : successPerCity.keySet()) {
                map.put(typeField, successPerCity.get(typeField));
            }
        }
        map.put(TransmigrationStatistic.FIELD_HIGH_SCORE,transmigrationStatistic.getHighScoreNum());
        map.put(TransmigrationStatistic.FIELD_NEW_RECORD,transmigrationStatistic.getNewRecordNum());
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
    public TransmigrationStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new TransmigrationStatistic(date,redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new TransmigrationStatistic(date, redisMap);
    }

    /**
     * 执行统计
     * @param ep
     */
    public void doStatistic (EPTransmigrationSuccess ep){
        long uid = ep.getGuId();
        String transmigrationBeginDate = ep.getTransmigrationBeginDate();
        int cityId = ep.getCityId();
        boolean isFirstSuccess = ep.getIsFirstSuccess();
        Integer score = ep.getScore();
        Boolean isNewRecord = ep.getIsNewRecord();
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //进行累计击败守将统计
        if (isFirstSuccess){
            //全量统计
            increment(uid, DateUtil.getTodayInt(), 1);
            //累计击败守将统计
            redisHashUtil.increment(key, transmigrationBeginDate, 1);
            CfgCityEntity city = CityTool.getCityById(cityId);
            if (city.getLevel() == 5){
                redisHashUtil.increment(key, city.getName(), 1);
            }
        }
        //刷新分数统计
        if (isNewRecord){
            redisHashUtil.increment(key, TransmigrationStatistic.FIELD_NEW_RECORD, 1);
        }
        //获得100分及以上评分统计
        if (score >= 100){
            redisHashUtil.increment(key, TransmigrationStatistic.FIELD_HIGH_SCORE, 1);
        }
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
