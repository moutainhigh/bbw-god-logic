package com.bbw.god.gameuser.statistic.behavior.fst;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 封神台统计统计service
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:30
 */
@Service
public class FstStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserCardService userCardService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FST;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FstStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        FstStatistic fstStatistic = (FstStatistic) statistic;
        Map<String, Integer> map = new HashMap<>();
        map.put(FST_CONTINUOUS_GRADING + UNDERLINE + NUM, fstStatistic.getContinuousGrading());
        map.put(FST_GUARD_WIN + UNDERLINE + NUM, fstStatistic.getGuardWin());
        map.put(FST_CARD + UNDERLINE + NUM, fstStatistic.getCard());
        map.put(FST_WINNUM + UNDERLINE + NUM, fstStatistic.getWinNum());
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
        return (T) getFstStatistic(redisMap);
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
    public FstStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getFstStatistic(redisMap);
    }

    private FstStatistic getFstStatistic(Map<String, Integer> redisMap) {
        Integer grading = redisMap.get(FST_CONTINUOUS_GRADING + UNDERLINE + NUM) == null ? 0 : redisMap.get(FST_CONTINUOUS_GRADING + UNDERLINE + NUM);
        Integer guard = redisMap.get(FST_GUARD_WIN + UNDERLINE + NUM) == null ? 0 : redisMap.get(FST_GUARD_WIN + UNDERLINE + NUM);
        Integer card = redisMap.get(FST_CARD + UNDERLINE + NUM) == null ? 0 : redisMap.get(FST_CARD + UNDERLINE + NUM);
        Integer winNum = redisMap.get(FST_WINNUM + UNDERLINE + NUM) == null ? 0 : redisMap.get(FST_WINNUM + UNDERLINE + NUM);
        return new FstStatistic(grading, guard, card, winNum);
    }

    public void draw(long uid, int cardNum, boolean guardWin, boolean isContinuousGrading, boolean continuous, boolean isWin) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        if (cardNum != 0) {
            redisHashUtil.increment(key, FST_CARD + UNDERLINE + NUM, cardNum);
        }
        if (guardWin) {
            redisHashUtil.increment(key, FST_GUARD_WIN + UNDERLINE + NUM, 1);
        }
        if (isContinuousGrading) {
            if (continuous) {
                redisHashUtil.increment(key, FST_CONTINUOUS_GRADING + UNDERLINE + NUM, 1);
            } else {
                redisHashUtil.putField(key, FST_CONTINUOUS_GRADING + UNDERLINE + NUM, 0);
            }
        }
        if (isWin) {
            redisHashUtil.increment(key, FST_WINNUM + UNDERLINE + NUM, 1);
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
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<Integer> fstCardIds = MallTool.getMallConfig().getFstCardIds();
        int count = 0;
        for (UserCard uCard : userCards) {
            if (fstCardIds.contains(CardTool.getNormalCardId(uCard.getBaseId()))) {
                count += 1;
            }
        }
        FstStatistic statistic = new FstStatistic();
        statistic.setCard(count);
        toRedis(uid, statistic);
    }
}
