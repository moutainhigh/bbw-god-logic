package com.bbw.god.gameuser.statistic.behavior.mixd;

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
 * 梦魇迷仙洞统计service
 *
 * @author lzc
 * @description
 * @date 2021/06/04 11:28
 */
@Service
public class NightmareMiXDStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.NIGHTMARE_MI_XD;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof NightmareMiXDStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        NightmareMiXDStatistic nightmareMiXDStatistic = (NightmareMiXDStatistic) statistic;
        Map<String, Integer> map = new HashMap<>();
        map.put(MI_XD_BEAT_PATROL + UNDERLINE + NUM, nightmareMiXDStatistic.getBeatPatrol());
        map.put(MI_XD_DRINK_WATER + UNDERLINE + NUM, nightmareMiXDStatistic.getDrinkWater());
        map.put(MI_XD_STEP_TRAP + UNDERLINE + NUM, nightmareMiXDStatistic.getStepTrap());
        map.put(MI_XD_OPEN_SPECIAL_BOX + UNDERLINE + NUM, nightmareMiXDStatistic.getOpenSpecialBox());
        map.put(MI_XD_BEAT_DEFIER + UNDERLINE + NUM, nightmareMiXDStatistic.getBeatDefier());
        map.put(MI_XD_PASS_OF_ONE_HP + UNDERLINE + NUM, nightmareMiXDStatistic.getPassOfOneHP());
        map.put(MI_XD_BITE_THE_DUST + UNDERLINE + NUM, nightmareMiXDStatistic.getBiteTheDust());
        map.put(MI_XD_DRINK_WATER_TO_ELEVEN + UNDERLINE + NUM, nightmareMiXDStatistic.getDrinkWaterToEleven());
        map.put(MI_XD_BEAT_PATROL_BOSS + UNDERLINE + NUM, nightmareMiXDStatistic.getBeatPatrolBoss());
        map.put(MI_XD_CONTINUOUS_SMELT_FAIL + UNDERLINE + NUM, nightmareMiXDStatistic.getContinuousSmeltFail());
        map.put(MI_XD_FULL_LIFE_PASS + UNDERLINE + NUM, nightmareMiXDStatistic.getFullLifePass());

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
        return (T) getNightmareMiXDStatistic(redisMap);
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
    public NightmareMiXDStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getNightmareMiXDStatistic(redisMap);
    }

    private NightmareMiXDStatistic getNightmareMiXDStatistic(Map<String, Integer> redisMap) {
        Integer beatPatrol = redisMap.get(MI_XD_BEAT_PATROL + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_BEAT_PATROL + UNDERLINE + NUM);
        Integer drinkWater = redisMap.get(MI_XD_DRINK_WATER + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_DRINK_WATER + UNDERLINE + NUM);
        Integer stepTrap = redisMap.get(MI_XD_STEP_TRAP + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_STEP_TRAP + UNDERLINE + NUM);
        Integer openSpecialBox = redisMap.get(MI_XD_OPEN_SPECIAL_BOX + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_OPEN_SPECIAL_BOX + UNDERLINE + NUM);
        Integer beatDefier = redisMap.get(MI_XD_BEAT_DEFIER + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_BEAT_DEFIER + UNDERLINE + NUM);
        Integer passOfOneHP = redisMap.get(MI_XD_PASS_OF_ONE_HP + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_PASS_OF_ONE_HP + UNDERLINE + NUM);
        Integer biteTheDust = redisMap.get(MI_XD_BITE_THE_DUST + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_BITE_THE_DUST + UNDERLINE + NUM);
        Integer drinkWaterToEleven = redisMap.get(MI_XD_DRINK_WATER_TO_ELEVEN + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_DRINK_WATER_TO_ELEVEN + UNDERLINE + NUM);
        Integer beatPatrolBoss = redisMap.get(MI_XD_BEAT_PATROL_BOSS + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_BEAT_PATROL_BOSS + UNDERLINE + NUM);
        Integer continuousSmeltFail = redisMap.get(MI_XD_CONTINUOUS_SMELT_FAIL + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_CONTINUOUS_SMELT_FAIL + UNDERLINE + NUM);
        Integer fullLifePass = redisMap.get(MI_XD_FULL_LIFE_PASS + UNDERLINE + NUM) == null ? 0 : redisMap.get(MI_XD_FULL_LIFE_PASS + UNDERLINE + NUM);
        return new NightmareMiXDStatistic(beatPatrol,drinkWater,stepTrap,openSpecialBox,beatDefier,passOfOneHP,biteTheDust,drinkWaterToEleven,beatPatrolBoss,continuousSmeltFail,fullLifePass);
    }

    public void draw(long uid, NightmareMiXDBehaviorType type, int value) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        switch (type){
            case BEAT_PATROL:
                redisHashUtil.increment(key, MI_XD_BEAT_PATROL + UNDERLINE + NUM, 1);
                if (value == 1) redisHashUtil.increment(key,  MI_XD_BEAT_PATROL_BOSS+ UNDERLINE + NUM, 1);
                break;
            case DRINK_WATER:
                redisHashUtil.increment(key,  MI_XD_DRINK_WATER+ UNDERLINE + NUM, 1);
                if (value == 1) redisHashUtil.increment(key,  MI_XD_DRINK_WATER_TO_ELEVEN+ UNDERLINE + NUM, 1);
                break;
            case STEP_TRAP:
                redisHashUtil.increment(key,  MI_XD_STEP_TRAP+ UNDERLINE + NUM, 1);
                break;
            case OPEN_SPECIAL_BOX:
                redisHashUtil.increment(key,  MI_XD_OPEN_SPECIAL_BOX+ UNDERLINE + NUM, 1);
                break;
            case BEAT_DEFIER:
                redisHashUtil.increment(key,  MI_XD_BEAT_DEFIER+ UNDERLINE + NUM, 1);
                break;
            case PASS_OF_ONE_HP:
                redisHashUtil.increment(key,  MI_XD_PASS_OF_ONE_HP+ UNDERLINE + NUM, 1);
                break;
            case BITE_THE_DUST:
                redisHashUtil.increment(key,  MI_XD_BITE_THE_DUST+ UNDERLINE + NUM, 1);
                break;
            case SMELT_FAIL:
                redisHashUtil.increment(key,  MI_XD_CONTINUOUS_SMELT_FAIL+ UNDERLINE + NUM, 1);
                break;
            case SMELT_SUCCEED:
                redisHashUtil.putField(key, MI_XD_CONTINUOUS_SMELT_FAIL+ UNDERLINE + NUM, 0);
            case FULL_LIFE_PASS:
                redisHashUtil.increment(key,  MI_XD_FULL_LIFE_PASS+ UNDERLINE + NUM, 1);
                break;
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
