package com.bbw.god.gameuser.statistic.behavior.sxdh;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.CombatResInfo;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.sxdhchallenge.UserSxdhSeasonTask;
import com.bbw.god.gameuser.task.sxdhchallenge.UserSxdhSeasonTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 神仙大会统计service
 * @date 2020/4/22 14:51
 */
@Service
public class SxdhStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSxdhSeasonTaskService userSxdhSeasonTaskService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.SXDH_FIGHT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof SxdhStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        SxdhStatistic sxdhStatistic = (SxdhStatistic) statistic;
        Integer date = sxdhStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(7);
        map.put(date + UNDERLINE + NUM, sxdhStatistic.getToday());
        map.put(TOTAL, sxdhStatistic.getTotal());
        map.put(date + UNDERLINE + WIN, sxdhStatistic.getTodayWin());
        map.put(WIN, sxdhStatistic.getTotalWin());
        map.put(CONTINUOUS_WIN, sxdhStatistic.getContinuousWin());
        map.put(JOIN_DAYS, sxdhStatistic.getJoinDays());
        Map<String, Integer> seasonWinMap = sxdhStatistic.getSeasonWinMap();
        Map<String, Integer> seasonJoinDaysMap = sxdhStatistic.getSeasonJoinDaysMap();
        Map<String, Integer> defeatHp = sxdhStatistic.getSeasonDefeatHpMap();
        Map<String, Integer> killCards = sxdhStatistic.getSeasonKillCardsMap();
        Map<String, Integer> changeCards = sxdhStatistic.getSeasonChangeCardsMap();
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
        Set<String> changeCardsKeySet = changeCards.keySet();
        for (String changCardsKey : changeCardsKeySet) {
            Integer val = changeCards.get(changCardsKey);
            map.put(CHANGE_CARDS + UNDERLINE + changCardsKey, val);
        }
        Map<String, Integer> middleSeasonRankMap = sxdhStatistic.getMiddleSeasonRankMap();
        Set<String> middleKeySet = middleSeasonRankMap.keySet();
        for (String middleKey : middleKeySet) {
            map.put(MIDDLE_SEASON_RANK + UNDERLINE + middleKey, middleSeasonRankMap.get(middleKey));
        }
        Map<String, Integer> seasonRankMap = sxdhStatistic.getSeasonRankMap();
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
        return (T) getSxdhStatistic(date, redisMap);
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
    public SxdhStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getSxdhStatistic(date, redisMap);
    }

    private SxdhStatistic getSxdhStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        String dateWinStr = date + UNDERLINE + WIN;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer todayWin = redisMap.get(dateWinStr) == null ? 0 : redisMap.get(dateWinStr);
        Integer win = redisMap.get(WIN) == null ? 0 : redisMap.get(WIN);
        Integer continuousWin = redisMap.get(CONTINUOUS_WIN) == null ? 0 : redisMap.get(CONTINUOUS_WIN);
        Integer joinDays = redisMap.get(JOIN_DAYS) == null ? 0 : redisMap.get(JOIN_DAYS);
        Map<String, Integer> winMap = new HashMap<>();
        Map<String, Integer> joinDaysMap = new HashMap<>();
        Map<String, Integer> defeatHpMap = new HashMap<>();
        Map<String, Integer> killCardsMap = new HashMap<>();
        Map<String, Integer> changeCardsMap = new HashMap<>();
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
            if (key.startsWith(CHANGE_CARDS)) {
                int season = Integer.parseInt(split[1]);
                Integer changeCards = redisMap.get(key);
                changeCardsMap.put(String.valueOf(season), changeCards);
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
        return new SxdhStatistic(today, total, date, joinDays, todayWin, win, continuousWin, winMap, joinDaysMap,
                defeatHpMap, killCardsMap, changeCardsMap, middleSeasonRankMap, seasonRankMap);
    }

    public void win(long uid, int date, long combatId, int season) {
        SxdhStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        statistic.addJoinDays(String.valueOf(season));
        statistic.setTodayWin(statistic.getTodayWin() + 1);
        statistic.setTotalWin(statistic.getTotalWin() + 1);
        statistic.addSeasonWin(String.valueOf(season));
        statistic.setContinuousWin(statistic.getContinuousWin() + 1);
        List<CombatResInfo> combatResInfos = combatService.combatResult(combatId);
        CombatResInfo info = combatResInfos.stream().filter(c -> uid == c.getUid()).findFirst().orElse(null);
        int killCards = info == null ? 0 : info.getOppoLostCard();
        int defeatHp = info == null ? 0 : info.getOppoLostHp();
        statistic.killCards(killCards, String.valueOf(season));
        statistic.defeatHp(defeatHp, String.valueOf(season));
        toRedis(uid, statistic);
    }

    public void lose(long uid, int date, long combatId, int season) {
        SxdhStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        statistic.addJoinDays(String.valueOf(season));
        statistic.setContinuousWin(0);
        List<CombatResInfo> combatResInfos = combatService.combatResult(combatId);
        CombatResInfo info = combatResInfos.stream().filter(c -> uid == c.getUid()).findFirst().orElse(null);
        int killCards = info == null ? 0 : info.getOppoLostCard();
        int defeatHp = info == null ? 0 : info.getOppoLostHp();
        statistic.killCards(killCards, String.valueOf(season));
        statistic.defeatHp(defeatHp, String.valueOf(season));
        toRedis(uid, statistic);
    }

    public void changeCards(long uid, int changeCardNum, int season) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, CHANGE_CARDS + UNDERLINE + season, changeCardNum);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
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
        UserAchievement continuousWinAchievement = userAchievementService.getUserAchievement(uid, 13570);
        int continuousWin = continuousWinAchievement == null ? 0 : continuousWinAchievement.getValue();
        SxdhStatistic statistic = new SxdhStatistic();
        statistic.setContinuousWin(continuousWin);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null != sxdhZone) {
            UserSxdhSeasonTask task8010 = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, 8010);
            if (null != task8010) {
                statistic.setJoinDays((int) task8010.getValue());
                statistic.getSeasonJoinDaysMap().put(String.valueOf(sxdhZone.getSeason()), (int) task8010.getValue());
            }
            UserSxdhSeasonTask task8020 = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, 8020);
            if (null != task8020) {
                statistic.getSeasonDefeatHpMap().put(String.valueOf(sxdhZone.getSeason()), (int) task8020.getValue());
            }
            UserSxdhSeasonTask task8030 = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, 8030);
            if (null != task8030) {
                statistic.getSeasonKillCardsMap().put(String.valueOf(sxdhZone.getSeason()), (int) task8030.getValue());
            }
            UserSxdhSeasonTask task8040 = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, 8040);
            if (null != task8040) {
                statistic.getSeasonChangeCardsMap().put(String.valueOf(sxdhZone.getSeason()), (int) task8040.getValue());
            }
            UserSxdhSeasonTask task8050 = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, 8050);
            if (null != task8050) {
                statistic.getSeasonWinMap().put(String.valueOf(sxdhZone.getSeason()), (int) task8050.getValue());
                statistic.setTotal((int) task8050.getValue());
                statistic.setTotalWin((int) task8050.getValue());
            }
        }
        toRedis(uid, statistic);
    }
}
