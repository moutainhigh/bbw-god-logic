package com.bbw.god.gameuser.statistic.behavior.card;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;


/**
 * @author suchaobin
 * @description 进阶卡牌统计service
 * @date 2020/4/21 15:46
 */
@Service
public class HierarchyCardStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.CARD_HIERARCHY;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof HierarchyCardStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        HierarchyCardStatistic hierarchyCardStatistic = (HierarchyCardStatistic) statistic;
        Integer date = hierarchyCardStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(17);
        map.put(date + UNDERLINE + NUM, hierarchyCardStatistic.getToday());
        map.put(TOTAL, hierarchyCardStatistic.getTotal());
        map.put(GOLD_CARD, hierarchyCardStatistic.getGoldCard());
        map.put(WOOD_CARD, hierarchyCardStatistic.getWoodCard());
        map.put(WATER_CARD, hierarchyCardStatistic.getWaterCard());
        map.put(FIRE_CARD, hierarchyCardStatistic.getFireCard());
        map.put(EARTH_CARD, hierarchyCardStatistic.getEarthCard());
        map.put(ONE_STAR, hierarchyCardStatistic.getOneStar());
        map.put(TWO_STAR, hierarchyCardStatistic.getTwoStar());
        map.put(THREE_STAR, hierarchyCardStatistic.getThreeStar());
        map.put(FOUR_STAR, hierarchyCardStatistic.getFourStar());
        map.put(FIVE_STAR, hierarchyCardStatistic.getFiveStar());
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
        return (T) getHierarchyCardStatistic(date, redisMap);
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
    public HierarchyCardStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getHierarchyCardStatistic(date, redisMap);
    }

    private HierarchyCardStatistic getHierarchyCardStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer gold = redisMap.get(GOLD_CARD) == null ? 0 : redisMap.get(GOLD_CARD);
        Integer wood = redisMap.get(WOOD_CARD) == null ? 0 : redisMap.get(WOOD_CARD);
        Integer water = redisMap.get(WATER_CARD) == null ? 0 : redisMap.get(WATER_CARD);
        Integer fire = redisMap.get(FIRE_CARD) == null ? 0 : redisMap.get(FIRE_CARD);
        Integer earth = redisMap.get(EARTH_CARD) == null ? 0 : redisMap.get(EARTH_CARD);
        Integer oneStar = redisMap.get(ONE_STAR) == null ? 0 : redisMap.get(ONE_STAR);
        Integer twoStar = redisMap.get(TWO_STAR) == null ? 0 : redisMap.get(TWO_STAR);
        Integer threeStar = redisMap.get(THREE_STAR) == null ? 0 : redisMap.get(THREE_STAR);
        Integer fourStar = redisMap.get(FOUR_STAR) == null ? 0 : redisMap.get(FOUR_STAR);
        Integer fiveStar = redisMap.get(FIVE_STAR) == null ? 0 : redisMap.get(FIVE_STAR);
        return new HierarchyCardStatistic(today, total, date, gold, wood, water, fire, earth, oneStar, twoStar,
                threeStar, fourStar, fiveStar);
    }

    public void hierarchyCard(long uid, int date, int cardType, int cardStar) {
        HierarchyCardStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.increment(cardType, cardStar);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 750);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        HierarchyCardStatistic statistic = new HierarchyCardStatistic();
        statistic.setToday(value);
        statistic.setTotal(value);
        toRedis(uid, statistic);
    }
}
