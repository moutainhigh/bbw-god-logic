package com.bbw.god.gameuser.statistic.resource.friend;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.FriendBuddy;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 好友统计service
 * @date 2020/4/16 14:03
 */
@Service
public class FriendResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private GameUserService gameUserService;

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
        return AwardEnum.FRIEND;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FriendStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        FriendStatistic friendStatistic = (FriendStatistic) statistic;
        int type = friendStatistic.getType();
        Integer date = friendStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(4);
        map.put(date + UNDERLINE + NUM, friendStatistic.getToday());
        map.put(TOTAL, friendStatistic.getTotal());
        String key = getKey(uid, StatisticTypeEnum.fromValue(type));
        redisHashUtil.putAllField(key, map);
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
        return (T) getFriendStatistic(typeEnum, date, redisMap);
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
    public FriendStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getFriendStatistic(typeEnum, date, redisMap);
    }

    private FriendStatistic getFriendStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        return new FriendStatistic(today, total, date, typeEnum.getValue());
    }

    /**
     * 新增好友
     *
     * @param uid  玩家id
     * @param date 日期
     */
    public void addFriend(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.GAIN);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        FriendBuddy friendBuddy = gameUserService.getSingleItem(uid, FriendBuddy.class);
        int size = 0;
        if (null != friendBuddy) {
            Set<Long> uids = friendBuddy.getFriendUids();
            size = uids.size();
        }
        toRedis(uid, new FriendStatistic(size, size, DateUtil.getTodayInt(),
                StatisticTypeEnum.GAIN.getValue()));
    }
}
