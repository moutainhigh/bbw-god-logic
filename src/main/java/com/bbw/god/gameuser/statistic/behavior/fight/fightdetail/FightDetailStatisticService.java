package com.bbw.god.gameuser.statistic.behavior.fight.fightdetail;

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
 * 战斗相关细节统计service
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:30
 */
@Service
public class FightDetailStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FIGHT_DETAIL;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FightDetailStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        FightDetailStatistic fightDetailStatistic = (FightDetailStatistic) statistic;
        Map<String, Integer> map = new HashMap<>();
        map.put(NIGHTMARE_MAIN_CITY_4325_LEADER_KILL + UNDERLINE + NUM, fightDetailStatistic.getNightmareMainCity4325LeaderKillNum());
        map.put(NIGHTMARE_MAIN_CITY_2539_LEADER_KILL + UNDERLINE + NUM, fightDetailStatistic.getNightmareMainCity2539LeaderKillNum());
        map.put(NIGHTMARE_MAIN_CITY_1024_LEADER_KILL + UNDERLINE + NUM, fightDetailStatistic.getNightmareMainCity1024LeaderKillNum());
        map.put(NIGHTMARE_MAIN_CITY_2608_LEADER_KILL + UNDERLINE + NUM, fightDetailStatistic.getNightmareMainCity2608LeaderKillNum());
        map.put(NIGHTMARE_MAIN_CITY_2725_LEADER_KILL + UNDERLINE + NUM, fightDetailStatistic.getNightmareMainCity2725LeaderKillNum());
        map.put(QI_LIN_BEAT, fightDetailStatistic.getQiLinBeat());
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
        return (T) getFightDetailStatistic(redisMap);
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
    public FightDetailStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getFightDetailStatistic(redisMap);
    }

    private FightDetailStatistic getFightDetailStatistic(Map<String, Integer> redisMap) {
        Integer city4325LeaderKillNum = redisMap.get(NIGHTMARE_MAIN_CITY_4325_LEADER_KILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(NIGHTMARE_MAIN_CITY_4325_LEADER_KILL + UNDERLINE + NUM);
        Integer city2539LeaderKillNum = redisMap.get(NIGHTMARE_MAIN_CITY_2539_LEADER_KILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(NIGHTMARE_MAIN_CITY_2539_LEADER_KILL + UNDERLINE + NUM);
        Integer city1024LeaderKillNum = redisMap.get(NIGHTMARE_MAIN_CITY_1024_LEADER_KILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(NIGHTMARE_MAIN_CITY_1024_LEADER_KILL + UNDERLINE + NUM);
        Integer city2608LeaderKillNum = redisMap.get(NIGHTMARE_MAIN_CITY_2608_LEADER_KILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(NIGHTMARE_MAIN_CITY_2608_LEADER_KILL + UNDERLINE + NUM);
        Integer city2725LeaderKillNum = redisMap.get(NIGHTMARE_MAIN_CITY_2725_LEADER_KILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(NIGHTMARE_MAIN_CITY_2725_LEADER_KILL + UNDERLINE + NUM);
        Integer qiLinBeat = redisMap.get(QI_LIN_BEAT) == null ? 0 : redisMap.get(QI_LIN_BEAT);
        return new FightDetailStatistic(city4325LeaderKillNum, city2539LeaderKillNum, city1024LeaderKillNum, city2608LeaderKillNum, city2725LeaderKillNum, qiLinBeat);
    }

    /**
     * @param uid
     * @param isCombatLeader    是否为主角卡战斗
     * @param cityId            城市ID
     * @param cityLeaderKillNum 主角卡战斗(主城)击杀卡牌数量
     * @param isQiLinBeat       是否为麒麟击败
     */
    public void draw(long uid, boolean isCombatLeader, int cityId, int cityLeaderKillNum, boolean isQiLinBeat) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        if (isCombatLeader && cityLeaderKillNum > 0) {
            switch (cityId) {
                case 4325:
                    redisHashUtil.increment(key, NIGHTMARE_MAIN_CITY_4325_LEADER_KILL + UNDERLINE + NUM, cityLeaderKillNum);
                    break;
                case 2539:
                    redisHashUtil.increment(key, NIGHTMARE_MAIN_CITY_2539_LEADER_KILL + UNDERLINE + NUM, cityLeaderKillNum);
                    break;
                case 1024:
                    redisHashUtil.increment(key, NIGHTMARE_MAIN_CITY_1024_LEADER_KILL + UNDERLINE + NUM, cityLeaderKillNum);
                    break;
                case 2608:
                    redisHashUtil.increment(key, NIGHTMARE_MAIN_CITY_2608_LEADER_KILL + UNDERLINE + NUM, cityLeaderKillNum);
                    break;
                case 2725:
                    redisHashUtil.increment(key, NIGHTMARE_MAIN_CITY_2725_LEADER_KILL + UNDERLINE + NUM, cityLeaderKillNum);
                    break;
            }
        }
        if (isQiLinBeat) {
            redisHashUtil.increment(key, QI_LIN_BEAT, 1);
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
