package com.bbw.god.gameuser.statistic.resource.card;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.GodException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 卡牌统计service
 * @date 2020/4/20 10:12
 */
@Service
public class CardResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 获取类型总数，例：城池统计只有获得，没有消耗，返回1 元宝统计，有获得也有消耗，返回2
     *
     * @return 类型总数
     */
    @Override
    public int getMyTypeCount() {
        return 1;
    }

    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.KP;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof CardStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        CardStatistic cardStatistic = (CardStatistic) statistic;
        Integer date = cardStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(17);
        map.put(date + UNDERLINE + NUM, statistic.getToday());
        map.put(TOTAL, statistic.getTotal());
        map.put(GOLD_CARD, cardStatistic.getGoldCardNum());
        map.put(WOOD_CARD, cardStatistic.getWoodCardNum());
        map.put(WATER_CARD, cardStatistic.getWaterCardNum());
        map.put(FIRE_CARD, cardStatistic.getFireCardNum());
        map.put(EARTH_CARD, cardStatistic.getEarthCardNum());
        Map<WayEnum, Integer> wayMap = cardStatistic.getFiveStarWayMap();
        Set<WayEnum> keySet = wayMap.keySet();
        for (WayEnum way : keySet) {
            map.put(STAR + 5 + UNDERLINE + way.getName(), wayMap.get(way));
        }
        Map<WayEnum, Integer> todayWayMap = cardStatistic.getTodayWayMap();
        Set<WayEnum> todayWayKeySet = todayWayMap.keySet();
        for (WayEnum way : todayWayKeySet) {
            map.put(date + UNDERLINE + way.getName(), todayWayMap.get(way));
        }
        Map<WayEnum, Integer> totalWayMap = cardStatistic.getTotalWayMap();
        Set<WayEnum> totalWayKeySet = totalWayMap.keySet();
        for (WayEnum way : totalWayKeySet) {
            map.put(way.getName(), totalWayMap.get(way));
        }
        String key = getKey(uid, StatisticTypeEnum.GAIN);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(redisKey);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) getCardStatistic(typeEnum, date, redisMap);
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
    public CardStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return getCardStatistic(typeEnum, date, redisMap);
    }

    private CardStatistic getCardStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer todayNum = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer totalNum = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer goldCard = redisMap.get(GOLD_CARD) == null ? 0 : redisMap.get(GOLD_CARD);
        Integer woodCard = redisMap.get(WOOD_CARD) == null ? 0 : redisMap.get(WOOD_CARD);
        Integer waterCard = redisMap.get(WATER_CARD) == null ? 0 : redisMap.get(WATER_CARD);
        Integer fireCard = redisMap.get(FIRE_CARD) == null ? 0 : redisMap.get(FIRE_CARD);
        Integer earthCard = redisMap.get(EARTH_CARD) == null ? 0 : redisMap.get(EARTH_CARD);
        Map<WayEnum, Integer> fiveStarWayMap = new HashMap<>(16);
        Map<WayEnum, Integer> todayWayMap = new HashMap<>(16);
        Map<WayEnum, Integer> totalWayMap = new HashMap<>(16);
        Set<String> keySet = redisMap.keySet();
        keySet.remove(dateNumStr);
        keySet.remove(TOTAL);
        keySet.remove(date + UNDERLINE + NUM);
        keySet.remove(GOLD_CARD);
        keySet.remove(WOOD_CARD);
        keySet.remove(WATER_CARD);
        keySet.remove(FIRE_CARD);
        keySet.remove(EARTH_CARD);
        String fiveStarStr = STAR + 5 + UNDERLINE;
        for (String key : keySet) {
            if (key.startsWith(fiveStarStr)) {
                String[] split = key.split(UNDERLINE);
                WayEnum way = WayEnum.fromName(split[1]);
                fiveStarWayMap.put(way, redisMap.get(key));
                continue;
            }
            if (key.contains(String.valueOf(date)) && key.contains(UNDERLINE) && !key.contains(NUM)) {
                String[] split = key.split(UNDERLINE);
                WayEnum way = WayEnum.fromName(split[1]);
                todayWayMap.put(way, redisMap.get(key));
                continue;
            }
            if (!key.contains(UNDERLINE)) {
                totalWayMap.put(WayEnum.fromName(key), redisMap.get(key));
            }
        }
        return new CardStatistic(todayNum, totalNum, date, typeEnum.getValue(), goldCard, woodCard, waterCard,
                fireCard, earthCard, fiveStarWayMap, todayWayMap, totalWayMap);
    }

    /**
     * 增加统计值
     *
     * @param uid      玩家id
     * @param date     日期
     * @param gold     金属性新增卡牌
     * @param wood     金属性新增卡牌
     * @param water    金属性新增卡牌
     * @param fire     金属性新增卡牌
     * @param earth    金属性新增卡牌
     * @param fiveStar 新增五星卡牌数
     * @param gainNum  获得卡牌数（包括重复的）
     * @param way      途径
     */
    public void increment(long uid, int date, int gold, int wood, int water, int fire, int earth, int fiveStar,
                          int gainNum, WayEnum way) {
        if (gold < 0 || wood < 0 || water < 0 || fire < 0 || earth < 0 || fiveStar < 0) {
            throw new GodException("统计增加值为负数");
        }
        CardStatistic cardStatistic = fromRedis(uid, StatisticTypeEnum.GAIN, date);
        cardStatistic.addCards(gold, wood, water, fire, earth, fiveStar, gainNum, way);
        toRedis(uid, cardStatistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        List<UserCard> userCards = userCardService.getUserCards(uid);
        Map<Integer, List<CfgCardEntity>> cardMap = userCards.stream().map(uc ->
                CardTool.getCardById(uc.getBaseId())).collect(Collectors.groupingBy(CfgCardEntity::getType));
        int gold = cardMap.get(TypeEnum.Gold.getValue()) == null ? 0 : cardMap.get(TypeEnum.Gold.getValue()).size();
        int wood = cardMap.get(TypeEnum.Wood.getValue()) == null ? 0 : cardMap.get(TypeEnum.Wood.getValue()).size();
        int water = cardMap.get(TypeEnum.Water.getValue()) == null ? 0 : cardMap.get(TypeEnum.Water.getValue()).size();
        int fire = cardMap.get(TypeEnum.Fire.getValue()) == null ? 0 : cardMap.get(TypeEnum.Fire.getValue()).size();
        int earth = cardMap.get(TypeEnum.Earth.getValue()) == null ? 0 : cardMap.get(TypeEnum.Earth.getValue()).size();
        int total = gold + water + wood + fire + earth;
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13780);
        int fiveStar = userAchievement == null ? 0 : userAchievement.getValue();
        increment(uid, DateUtil.getTodayInt(), gold, wood, water, fire, earth, fiveStar, total, WayEnum.JXZ_AWARD);
    }
}
