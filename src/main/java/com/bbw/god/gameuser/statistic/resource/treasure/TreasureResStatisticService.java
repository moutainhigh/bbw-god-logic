package com.bbw.god.gameuser.statistic.resource.treasure;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 法宝统计service
 * @date 2020/4/17 11:46
 */
@Service
public class TreasureResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserAchievementService userAchievementService;

    private List<Integer> attackSymbolList = Arrays.asList(20110, 20120, 20130, 20140, 20150, 20160);
    private List<Integer> defendSymbolList = Arrays.asList(20210, 20220, 20230, 20240, 20250, 20260);

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
        return AwardEnum.FB;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof TreasureStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        TreasureStatistic treasureStatistic = (TreasureStatistic) statistic;
        Integer date = treasureStatistic.getDate();
        int type = treasureStatistic.getType();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, treasureStatistic.getToday());
        map.put(TOTAL, treasureStatistic.getTotal());
        Map<String, Map<WayEnum, Integer>> todayMap = treasureStatistic.getTodayMap();
        Set<String> todayKeySet = todayMap.keySet();
        for (String todayTreasure : todayKeySet) {
            Map<WayEnum, Integer> todayWayMap = todayMap.get(todayTreasure);
            Set<WayEnum> todayWays = todayWayMap.keySet();
            for (WayEnum todayWay : todayWays) {
                Integer value = todayWayMap.get(todayWay);
                map.put(date + UNDERLINE + todayTreasure + UNDERLINE + todayWay.getName(), value);
            }
        }
        Map<String, Map<WayEnum, Integer>> totalMap = treasureStatistic.getTotalMap();
        Set<String> totalKeySet = totalMap.keySet();
        for (String totalTreasure : totalKeySet) {
            Map<WayEnum, Integer> totalWayMap = totalMap.get(totalTreasure);
            Set<WayEnum> totalWays = totalWayMap.keySet();
            for (WayEnum totalWay : totalWays) {
                Integer value = totalWayMap.get(totalWay);
                map.put(totalTreasure + UNDERLINE + totalWay.getName(), value);
            }
        }
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
        return (T) getTreasureStatistic(typeEnum, date, redisMap);
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
    public TreasureStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getTreasureStatistic(typeEnum, date, redisMap);
    }

    private TreasureStatistic getTreasureStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        redisMap.remove(dateNumStr);
        redisMap.remove(TOTAL);
        Map<String, Map<WayEnum, Integer>> todayMap = new HashMap<>(16);
        Map<String, Map<WayEnum, Integer>> totalMap = new HashMap<>(16);
        Set<String> keySet = redisMap.keySet();
        for (String key : keySet) {
            String[] splitKey = key.split(UNDERLINE);
            String rex = "^20\\d{6}";
            Pattern p = Pattern.compile(rex);
            Integer value = redisMap.get(key);
            if (!p.matcher(splitKey[0]).matches()) {
                CfgTreasureEntity treasure = null;
                try {
                    treasure = TreasureTool.getTreasureByName(splitKey[splitKey.length - 2]);
                } catch (Exception e) {
                    continue;
                }
                WayEnum way = WayEnum.fromName(splitKey[splitKey.length - 1]);
                Map<WayEnum, Integer> wayMap = totalMap.get(treasure.getName());
                if (null == wayMap || wayMap.size() == 0) {
                    wayMap = new HashMap<>(16);
                }
                wayMap.put(way, value);
                totalMap.put(treasure.getName(), wayMap);
                continue;
            }
            if (splitKey.length == 3 && p.matcher(splitKey[0]).matches() && date == Integer.parseInt(splitKey[0])) {
                CfgTreasureEntity treasure = TreasureTool.getTreasureByName(splitKey[splitKey.length - 2]);
                WayEnum way = WayEnum.fromName(splitKey[splitKey.length - 1]);
                Map<WayEnum, Integer> wayMap = todayMap.get(treasure.getName());
                if (null == wayMap || wayMap.size() == 0) {
                    wayMap = new HashMap<>(16);
                }
                wayMap.put(way, value);
                todayMap.put(treasure.getName(), wayMap);
            }
        }
        return new TreasureStatistic(today, total, date, typeEnum.getValue(), todayMap, totalMap);
    }

    /**
     * 添加统计值
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @param treasure 法宝
     * @param way      途径
     * @param addValue 增加值
     */
    public void increment(long uid, StatisticTypeEnum typeEnum, int date, CfgTreasureEntity treasure, WayEnum way,
                          int addValue) {
        if (addValue < 0) {
            throw new GodException("统计增加值为负数");
        }
        TreasureStatistic statistic = fromRedis(uid, typeEnum, date);
        statistic.increment(treasure, way, addValue);
        toRedis(uid, statistic);
    }

    public void increment(long uid, StatisticTypeEnum typeEnum, int date, List<EVTreasure> treasureList, WayEnum way) {
        TreasureStatistic statistic = fromRedis(uid, typeEnum, date);
        for (EVTreasure evTreasure : treasureList) {
            Integer treasureId = evTreasure.getId();
            CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
            Integer num = evTreasure.getNum();
            statistic.increment(treasure, way, num);
        }
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement secretSkillScrollAchievement = userAchievementService.getUserAchievement(uid, 13260);
        UserAchievement skillScrollAchievement = userAchievementService.getUserAchievement(uid, 13230);
        UserAchievement attackAchievement = userAchievementService.getUserAchievement(uid, 13270);
        UserAchievement defendAchievement = userAchievementService.getUserAchievement(uid, 13280);
        UserAchievement bbxAchievement = userAchievementService.getUserAchievement(uid, 13690);
        UserAchievement xrdAchievement = userAchievementService.getUserAchievement(uid, 13760);
        int secretSkillScroll = secretSkillScrollAchievement == null ? 0 : secretSkillScrollAchievement.getValue();
        int skillScorll = skillScrollAchievement == null ? 0 : skillScrollAchievement.getValue();
        int attack = attackAchievement == null ? 0 : attackAchievement.getValue();
        int defend = defendAchievement == null ? 0 : defendAchievement.getValue();
        int bbx = bbxAchievement == null ? 0 : bbxAchievement.getValue();
        int xrd = xrdAchievement == null ? 0 : xrdAchievement.getValue();
        TreasureStatistic treasureStatistic = new TreasureStatistic(StatisticTypeEnum.GAIN.getValue());
        treasureStatistic.increment(TreasureTool.getTreasureById(21163), WayEnum.BYPALACE_GET_CHAPTER_AWARD,
                secretSkillScroll);
        treasureStatistic.increment(TreasureTool.getTreasureById(21001), WayEnum.BYPALACE_GET_CHAPTER_AWARD,
                skillScorll - secretSkillScroll);
        treasureStatistic.increment(TreasureTool.getTreasureById(520), WayEnum.BBX_PICK, bbx);
        treasureStatistic.increment(TreasureTool.getTreasureById(190), WayEnum.XRD, xrd);
        for (int i = 0; i < attack; i++) {
            Integer attackId = attackSymbolList.get(i);
            treasureStatistic.increment(TreasureTool.getTreasureById(attackId), WayEnum.BYPALACE_GET_CHAPTER_AWARD, 1);
        }
        for (int i = 0; i < defend; i++) {
            Integer defendId = defendSymbolList.get(i);
            treasureStatistic.increment(TreasureTool.getTreasureById(defendId), WayEnum.BYPALACE_GET_CHAPTER_AWARD, 1);
        }
        toRedis(uid, treasureStatistic);

        UserAchievement cocAchievement = userAchievementService.getUserAchievement(uid, 13460);
        UserAchievement tradeAchievement = userAchievementService.getUserAchievement(uid, 13470);
        int coc = cocAchievement == null ? 0 : cocAchievement.getValue();
        int trade = tradeAchievement == null ? 0 : tradeAchievement.getValue();
        TreasureStatistic statistic = new TreasureStatistic(StatisticTypeEnum.CONSUME.getValue());
        statistic.increment(TreasureTool.getTreasureById(10050), WayEnum.TREASURE_USE, coc);
        statistic.increment(TreasureTool.getTreasureById(10060), WayEnum.TREASURE_USE, trade);
        toRedis(uid, statistic);
    }
}
