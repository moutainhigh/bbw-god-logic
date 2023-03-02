package com.bbw.god.gameuser.statistic.behavior.biyou;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.biyoupalace.UserBYPalaceLockSkill;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 碧游宫service
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:30
 */
@Service
public class BiYouStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BI_YOU;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof BiYouStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        BiYouStatistic biYouStatistic = (BiYouStatistic) statistic;
        Integer date = biYouStatistic.getDate();
        Map<String, Integer> map = new HashMap<>();
        map.put(date + UNDERLINE + NUM, biYouStatistic.getToday());
        map.put(TOTAL, biYouStatistic.getTotal());
        map.put(BI_YOU_MZ_SKILL + UNDERLINE + NUM, biYouStatistic.getMzSkillNum());
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
        return (T) getBiYouStatistic(date, redisMap);
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
    public BiYouStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getBiYouStatistic(date, redisMap);
    }

    private BiYouStatistic getBiYouStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer mzSkillNum = redisMap.get(BI_YOU_MZ_SKILL + UNDERLINE + NUM) == null ? 0 : redisMap.get(BI_YOU_MZ_SKILL + UNDERLINE + NUM);
        return new BiYouStatistic(today, total, date, mzSkillNum);
    }

    public void draw(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, BI_YOU_MZ_SKILL + UNDERLINE + NUM, 1);
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
        BiYouStatistic statistic = new BiYouStatistic();
        UserBYPalaceLockSkill lockSkill = this.gameUserService.getSingleItem(uid, UserBYPalaceLockSkill.class);
        int mzSkillNum = lockSkill == null ? 0 : lockSkill.getSkillNumByMZ();
        statistic.setMzSkillNum(mzSkillNum);
        statistic.setTotal(mzSkillNum);
        toRedis(uid, statistic);
    }
}
