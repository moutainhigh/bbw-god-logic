package com.bbw.god.gameuser.statistic.behavior.juling;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.card.juling.UserJuLJInfo;
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
 * @description 聚灵统计service
 * @date 2020/4/23 11:53
 */
@Service
public class JuLingStatisticService extends BehaviorStatisticService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.JU_LING;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof JuLingStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        JuLingStatistic juLingStatistic = (JuLingStatistic) statistic;
        Integer date = juLingStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, juLingStatistic.getToday());
        map.put(TOTAL, juLingStatistic.getTotal());
        map.put(LAST_CARD, juLingStatistic.getLastCardId());
        map.put(CONTINUOUS_SOME_CARD, juLingStatistic.getContinuousSomeCard());
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
        return (T) getJuLingStatistic(date, redisMap);
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
    public JuLingStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getJuLingStatistic(date, redisMap);
    }

    private JuLingStatistic getJuLingStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer lastCard = redisMap.get(LAST_CARD) == null ? 0 : redisMap.get(LAST_CARD);
        Integer continuos = redisMap.get(CONTINUOUS_SOME_CARD) == null ? 0 : redisMap.get(CONTINUOUS_SOME_CARD);
        return new JuLingStatistic(today, total, date, lastCard, continuos);
    }

    public void juLing(long uid, int date, int cardId) {
        JuLingStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        if (cardId == statistic.getLastCardId()) {
            statistic.setContinuousSomeCard(statistic.getContinuousSomeCard() + 1);
        }
        statistic.setLastCardId(cardId);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        UserJuLJInfo juLJInfo = gameUserService.getSingleItem(uid, UserJuLJInfo.class);
        if (null == juLJInfo) {
            return;
        }
        Integer lastJxqCard = juLJInfo.getLastJxqCard();
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 13890);
        Integer value = userAchievement == null ? 0 : userAchievement.getValue();
        JuLingStatistic statistic = new JuLingStatistic();
        statistic.setLastCardId(lastJxqCard);
        statistic.setContinuousSomeCard(value);
        toRedis(uid, statistic);
    }
}
