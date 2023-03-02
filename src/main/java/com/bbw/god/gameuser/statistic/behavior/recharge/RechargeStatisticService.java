package com.bbw.god.gameuser.statistic.behavior.recharge;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 充值统计service
 * @date 2020/7/3 15:10
 */
@Service
public class RechargeStatisticService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.RECHARGE;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof RechargeStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        RechargeStatistic rechargeStatistic = (RechargeStatistic) statistic;
        Integer date = rechargeStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(4);
        map.put(date + UNDERLINE + NUM, rechargeStatistic.getToday());
        map.put(TOTAL, rechargeStatistic.getTotal());
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
        return (T) getRechargeStatistic(date, redisMap);
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
    public RechargeStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getRechargeStatistic(date, redisMap);
    }

    private RechargeStatistic getRechargeStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        return new RechargeStatistic(today, total, date);
    }

    public RechargeStatistic fromRedis(long uid, int date) {
        return fromRedis(uid, StatisticTypeEnum.NONE, date);
    }

    public void recharge(long uid, int date, int num) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, num);
        redisHashUtil.increment(key, TOTAL, num);
        statisticPool.toUpdatePool(key);
    }

    public int getTodayRecharge(long uid) {
        RechargeStatistic rechargeStatistic = fromRedis(uid, DateUtil.getTodayInt());
        return rechargeStatistic.getToday();
    }

    public int getTotalRecharge(long uid) {
        RechargeStatistic rechargeStatistic = fromRedis(uid, DateUtil.getTodayInt());
        return rechargeStatistic.getTotal();
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        redisHashUtil.delete(getKey(uid, StatisticTypeEnum.NONE));
        int sid = gameUserService.getActiveSid(uid);
        String userName = gameUserService.getGameUser(uid).getRoleInfo().getUserName();
        Optional<InsRoleInfoEntity> optional = insRoleInfoService.getUidAtLoginServer(sid, userName);
        if (optional.isPresent()) {
            InsRoleInfoEntity insRoleInfoEntity = optional.get();
            Integer pay = insRoleInfoEntity.getPay();
            recharge(uid, DateUtil.getTodayInt(), pay);
        }
    }
}
