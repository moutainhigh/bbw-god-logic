package com.bbw.god.gameuser.statistic.behavior.zhibao;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.Enum.ZhiBaoEnum;
import com.bbw.god.gameuser.card.equipment.event.EPCardZhiBaoAdd;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 至宝行为统计service
 *
 * @author: huanghb
 * @date: 2022/5/20 17:26
 */
@Service
public class ZhiBaoStatistucService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.KUNLS_INFUSION;
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
        if (!(statistic instanceof ZhiBaoStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        ZhiBaoStatistic zhiBaoStatistic = (ZhiBaoStatistic) statistic;
        Integer date = zhiBaoStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, zhiBaoStatistic.getToday());
        map.put(TOTAL, zhiBaoStatistic.getTotal());
        map.put(ZhiBaoStatistic.GOLD_PROPERTY_FAQI_NUM, zhiBaoStatistic.getGoldPropertyFaQiNum());
        map.put(ZhiBaoStatistic.GOLD_PROPERTY_LINGBAO_NUM, zhiBaoStatistic.getGoldPropertyLingBaoNum());
        map.put(ZhiBaoStatistic.WOOD_PROPERTY_FAQI_NUM, zhiBaoStatistic.getWoodPropertyFaQiNum());
        map.put(ZhiBaoStatistic.WOOD_PROPERTY_LINGBAO_NUM, zhiBaoStatistic.getWoodPropertyLingBaoNum());
        map.put(ZhiBaoStatistic.WATER_PROPERTY_FAQI_NUM, zhiBaoStatistic.getWaterPropertyFaQiNum());
        map.put(ZhiBaoStatistic.WATER_PROPERTY_LINGBAO_NUM, zhiBaoStatistic.getWaterPropertyLingBaoNum());
        map.put(ZhiBaoStatistic.FIRE_PROPERTY_FAQI_NUM, zhiBaoStatistic.getFirePropertyFaQiNum());
        map.put(ZhiBaoStatistic.FIRE_PROPERTY_LINGBAO_NUM, zhiBaoStatistic.getFirePropertyLingBaoNum());
        map.put(ZhiBaoStatistic.EARTH_PROPERTY_FAQI_NUM, zhiBaoStatistic.getEarthPropertyFaQiNum());
        map.put(ZhiBaoStatistic.EARTH_PROPERTY_LINGBAO_NUM, zhiBaoStatistic.getEarthPropertyLingBaoNum());
        map.put(ZhiBaoStatistic.ZHIBAO_NUM, zhiBaoStatistic.getZhiBaoNum());
        map.put(ZhiBaoStatistic.FULL_ATTACK_NUM, zhiBaoStatistic.getFullAttackNum());
        map.put(ZhiBaoStatistic.FULL_DEFENSE_NUM, zhiBaoStatistic.getFullDefenseNum());
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
    public ZhiBaoStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new ZhiBaoStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new ZhiBaoStatistic(date, redisMap);
    }

    /**
     * 至宝获得统计
     *
     * @param uid
     */
    public void addZhiBaoStatistic(long uid, EPCardZhiBaoAdd ep) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //至宝统计
        if (null == ep.getZhiBaoId() || 0 == ep.getZhiBaoId()) {
            return;
        }
        int quality = ep.getZhiBaoId() % 100;

        int zhiBaoType = ep.getZhiBaoId() / 100;
        if (QualityEnum.BEST_QUALITY.getValue() == quality && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FAIRY_FAQI_NUM, 1);
        }
        if (QualityEnum.BEST_QUALITY.getValue() == quality && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FAIRY_LINGBAO_NUM, 1);
        }
        if (TypeEnum.Gold.getValue() == ep.getProperty() && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.GOLD_PROPERTY_FAQI_NUM, 1);
        }
        if (TypeEnum.Gold.getValue() == ep.getProperty() && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.GOLD_PROPERTY_LINGBAO_NUM, 1);
        }
        if (TypeEnum.Wood.getValue() == ep.getProperty() && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.WOOD_PROPERTY_FAQI_NUM, 1);
        }
        if (TypeEnum.Wood.getValue() == ep.getProperty() && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.WOOD_PROPERTY_LINGBAO_NUM, 1);
        }
        if (TypeEnum.Water.getValue() == ep.getProperty() && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.WATER_PROPERTY_FAQI_NUM, 1);
        }
        if (TypeEnum.Water.getValue() == ep.getProperty() && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.WATER_PROPERTY_LINGBAO_NUM, 1);
        }
        if (TypeEnum.Fire.getValue() == ep.getProperty() && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FIRE_PROPERTY_FAQI_NUM, 1);
        }
        if (TypeEnum.Fire.getValue() == ep.getProperty() && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FIRE_PROPERTY_LINGBAO_NUM, 1);
        }
        if (TypeEnum.Earth.getValue() == ep.getProperty() && ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.EARTH_PROPERTY_FAQI_NUM, 1);
        }
        if (TypeEnum.Earth.getValue() == ep.getProperty() && ZhiBaoEnum.LING_BAO.getValue() == zhiBaoType) {
            redisHashUtil.increment(key, ZhiBaoStatistic.EARTH_PROPERTY_LINGBAO_NUM, 1);
        }
        redisHashUtil.increment(key, ZhiBaoStatistic.ZHIBAO_NUM, ep.getZhiBaoNum());
        if (0 != ep.getFullAttackNum()) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FULL_ATTACK_NUM, ep.getFullAttackNum());
        }
        if (0 != ep.getFullDefenseNum()) {
            redisHashUtil.increment(key, ZhiBaoStatistic.FULL_DEFENSE_NUM, ep.getFullDefenseNum());
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
