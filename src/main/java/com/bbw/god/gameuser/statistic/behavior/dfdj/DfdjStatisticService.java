package com.bbw.god.gameuser.statistic.behavior.dfdj;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.CombatResInfo;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 巅峰对决统计service
 * @date 2020/4/22 14:51
 */
@Service
public class DfdjStatisticService extends BehaviorStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private CombatRedisService combatService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.DFDJ_FIGHT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof DfdjStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        DfdjStatistic dfdjStatistic = (DfdjStatistic) statistic;
        Integer date = dfdjStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(7);
        map.put(date + UNDERLINE + NUM, dfdjStatistic.getToday());
        map.put(TOTAL, dfdjStatistic.getTotal());
        map.put(date + UNDERLINE + WIN, dfdjStatistic.getTodayWin());
        map.put(WIN, dfdjStatistic.getTotalWin());
        map.put(JOIN_DAYS, dfdjStatistic.getJoinDays());
        Map<String, Integer> seasonWinMap = dfdjStatistic.getSeasonWinMap();
        Map<String, Integer> seasonJoinDaysMap = dfdjStatistic.getSeasonJoinDaysMap();
        Map<String, Integer> defeatHp = dfdjStatistic.getSeasonDefeatHpMap();
        Map<String, Integer> killCards = dfdjStatistic.getSeasonKillCardsMap();
        Set<String> seasonWinKeySet = seasonWinMap.keySet();
        for (String seasonWinKey : seasonWinKeySet) {
            Integer val = seasonWinMap.get(seasonWinKey);
            map.put(WIN + UNDERLINE + seasonWinKey, val);
        }
        Set<String> joinDaysKeySet = seasonJoinDaysMap.keySet();
        for (String joinDaysKey : joinDaysKeySet) {
            Integer val = seasonJoinDaysMap.get(joinDaysKey);
            map.put(JOIN_DAYS + UNDERLINE + joinDaysKey, val);
        }
        Set<String> defeatHpKeySet = defeatHp.keySet();
        for (String defeatHpKey : defeatHpKeySet) {
            Integer val = defeatHp.get(defeatHpKey);
            map.put(DEFEAT_HP + UNDERLINE + defeatHpKey, val);
        }
        Set<String> killCardsKeySet = killCards.keySet();
        for (String killCardsKey : killCardsKeySet) {
            Integer val = killCards.get(killCardsKey);
            map.put(KILL_CARDS + UNDERLINE + killCardsKey, val);
        }
        Map<String, Integer> middleSeasonRankMap = dfdjStatistic.getMiddleSeasonRankMap();
        Set<String> middleKeySet = middleSeasonRankMap.keySet();
        for (String middleKey : middleKeySet) {
            map.put(MIDDLE_SEASON_RANK + UNDERLINE + middleKey, middleSeasonRankMap.get(middleKey));
        }
        Map<String, Integer> seasonRankMap = dfdjStatistic.getSeasonRankMap();
        Set<String> seasonKeySet = seasonRankMap.keySet();
        for (String seasonKey : seasonKeySet) {
            map.put(SEASON_RANK + UNDERLINE + seasonKey, seasonRankMap.get(seasonKey));
        }
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
        return (T) getDfdjStatistic(date, redisMap);
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
    public DfdjStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getDfdjStatistic(date, redisMap);
    }

    private DfdjStatistic getDfdjStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        String dateWinStr = date + UNDERLINE + WIN;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer todayWin = redisMap.get(dateWinStr) == null ? 0 : redisMap.get(dateWinStr);
        Integer win = redisMap.get(WIN) == null ? 0 : redisMap.get(WIN);
        Integer joinDays = redisMap.get(JOIN_DAYS) == null ? 0 : redisMap.get(JOIN_DAYS);
        Map<String, Integer> winMap = new HashMap<>();
        Map<String, Integer> joinDaysMap = new HashMap<>();
        Map<String, Integer> defeatHpMap = new HashMap<>();
        Map<String, Integer> killCardsMap = new HashMap<>();
        Map<String, Integer> middleSeasonRankMap = new HashMap<>();
        Map<String, Integer> seasonRankMap = new HashMap<>();
        Set<String> keySet = redisMap.keySet();
        for (String key : keySet) {
            String[] split = key.split(UNDERLINE);
            if (key.startsWith(WIN) && key.contains(UNDERLINE)) {
                int season = Integer.parseInt(split[1]);
                Integer seasonWin = redisMap.get(key);
                winMap.put(String.valueOf(season), seasonWin);
                continue;
            }
            if (key.startsWith(JOIN_DAYS) && key.contains(UNDERLINE)) {
                int season = Integer.parseInt(split[1]);
                Integer seasonJoinDays = redisMap.get(key);
                joinDaysMap.put(String.valueOf(season), seasonJoinDays);
                continue;
            }
            if (key.startsWith(DEFEAT_HP)) {
                int season = Integer.parseInt(split[1]);
                Integer defeatHp = redisMap.get(key);
                defeatHpMap.put(String.valueOf(season), defeatHp);
                continue;
            }
            if (key.startsWith(KILL_CARDS)) {
                int season = Integer.parseInt(split[1]);
                Integer killCards = redisMap.get(key);
                killCardsMap.put(String.valueOf(season), killCards);
                continue;
            }
            if (key.startsWith(MIDDLE_SEASON_RANK)) {
                int season = Integer.parseInt(split[1]);
                Integer rank = redisMap.get(key);
                middleSeasonRankMap.put(String.valueOf(season), rank);
                continue;
            }
            if (key.startsWith(SEASON_RANK)) {
                int season = Integer.parseInt(split[1]);
                Integer rank = redisMap.get(key);
                seasonRankMap.put(String.valueOf(season), rank);
            }
        }
        return new DfdjStatistic(today, total, date, joinDays, todayWin, win, winMap, joinDaysMap,
                defeatHpMap, killCardsMap, middleSeasonRankMap, seasonRankMap);
    }

    public void win(long uid, int date, long combatId, int season) {
        DfdjStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        statistic.addJoinDays(String.valueOf(season));
        statistic.setTodayWin(statistic.getTodayWin() + 1);
        statistic.setTotalWin(statistic.getTotalWin() + 1);
        statistic.addSeasonWin(String.valueOf(season));
        List<CombatResInfo> combatResInfos = combatService.combatResult(combatId);
        CombatResInfo info = combatResInfos.stream().filter(c -> uid == c.getUid()).findFirst().orElse(null);
        int killCards = info == null ? 0 : info.getOppoLostCard();
        int defeatHp = info == null ? 0 : info.getOppoLostHp();
        statistic.killCards(killCards, String.valueOf(season));
        statistic.defeatHp(defeatHp, String.valueOf(season));
        toRedis(uid, statistic);
    }

    public void lose(long uid, int date, long combatId, int season) {
        DfdjStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        statistic.addJoinDays(String.valueOf(season));
        List<CombatResInfo> combatResInfos = combatService.combatResult(combatId);
        CombatResInfo info = combatResInfos.stream().filter(c -> uid == c.getUid()).findFirst().orElse(null);
        int killCards = info == null ? 0 : info.getOppoLostCard();
        int defeatHp = info == null ? 0 : info.getOppoLostHp();
        statistic.killCards(killCards, String.valueOf(season));
        statistic.defeatHp(defeatHp, String.valueOf(season));
        toRedis(uid, statistic);
    }

    /**
     * 更新排名
     *
     * @param uid      玩家id
     * @param rankType 排行类型
     * @param season   赛季
     * @param rank     排名
     */
    public void updateRank(long uid, SxdhRankType rankType, int season, int rank) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        switch (rankType) {
            case MIDDLE_RANK:
                redisHashUtil.increment(key, MIDDLE_SEASON_RANK + UNDERLINE + season, rank);
                break;
            case RANK:
                redisHashUtil.increment(key, SEASON_RANK + UNDERLINE + season, rank);
                break;
            default:
                return;
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
