package com.bbw.god.gameuser.statistic.behavior.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.server.flx.FlxService;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 福临轩统计service
 * @date 2020/4/23 10:26
 */
@Service
public class FlxStatisticService extends BehaviorStatisticService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private FlxService flxService;
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FLX;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof FlxStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        FlxStatistic flxStatistic = (FlxStatistic) statistic;
        Integer date = flxStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(9);
        map.put(date + UNDERLINE + NUM, flxStatistic.getToday());
        map.put(TOTAL, flxStatistic.getTotal());
        map.put(FLX_SG, flxStatistic.getCaishuzi());
        map.put(FLX_SG + UNDERLINE + WIN, flxStatistic.getCaishuziWin());
        map.put(FLX_YSG, flxStatistic.getYayale());
        map.put(FLX_YSG + UNDERLINE + WIN, flxStatistic.getYayaleWin());
        map.put(CONTINUOUS_LOSE_DAYS, flxStatistic.getContinuousLoseDays());
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
        return (T) getFlxStatistic(date, redisMap);
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
    public FlxStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getFlxStatistic(date, redisMap);
    }

    private FlxStatistic getFlxStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer caishuzi = redisMap.get(FLX_SG) == null ? 0 : redisMap.get(FLX_SG);
        Integer caishuziWin = redisMap.get(FLX_SG_UNDERLINE_WIN) == null ? 0 :
                redisMap.get(FLX_SG_UNDERLINE_WIN);
        Integer yayale = redisMap.get(FLX_YSG) == null ? 0 : redisMap.get(FLX_YSG);
        Integer yayaleWin = redisMap.get(FLX_YSG_UNDERLINE_WIN) == null ? 0 :
                redisMap.get(FLX_YSG_UNDERLINE_WIN);
        Integer continuiusLoseDays = redisMap.get(CONTINUOUS_LOSE_DAYS) == null ? 0 :
                redisMap.get(CONTINUOUS_LOSE_DAYS);
        return new FlxStatistic(today, total, date, caishuzi, caishuziWin, yayale, yayaleWin, continuiusLoseDays);
    }

    public void caishuziBet(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        int yesterday = DateUtil.toDateInt(DateUtil.addDays(DateUtil.now(), -1));
        FlxStatistic yesterdayStatistic = fromRedis(uid, StatisticTypeEnum.NONE, yesterday);
        FlxStatistic todayStatistic = fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (0 == yesterdayStatistic.getToday() && 0 == todayStatistic.getToday()) {
            redisHashUtil.putField(key, CONTINUOUS_LOSE_DAYS, 0);
        }
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, FLX_SG, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void yayaleBet(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        int yesterday = DateUtil.toDateInt(DateUtil.addDays(DateUtil.now(), -1));
        FlxStatistic yesterdayStatistic = fromRedis(uid, StatisticTypeEnum.NONE, yesterday);
        FlxStatistic todayStatistic = fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (0 == yesterdayStatistic.getToday() && 0 == todayStatistic.getToday()) {
            redisHashUtil.putField(key, CONTINUOUS_LOSE_DAYS, 0);
        }
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        redisHashUtil.increment(key, FLX_YSG, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void caishuziWin(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, FLX_SG + UNDERLINE + WIN, 1);
        redisHashUtil.putField(key, CONTINUOUS_LOSE_DAYS, 0);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void yayaleWin(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, FLX_YSG + UNDERLINE + WIN, 1);
        redisHashUtil.putField(key, CONTINUOUS_LOSE_DAYS, 0);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void caishuziFail(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        GameUser gu = gameUserService.getGameUser(uid);
        Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
        // 福临轩奖励是先发押押乐再发猜数字，所以猜数字失败要先判断是否下注过押押乐
        List<FlxYaYaLeBet> betList = flxService.getYaYaLeBetResult(uid, gu.getServerId(),
                DateUtil.toDateInt(yesterday));
        if (ListUtil.isEmpty(betList)) {
            redisHashUtil.increment(key, CONTINUOUS_LOSE_DAYS, 1);
        }
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void yayaleFail(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, CONTINUOUS_LOSE_DAYS, 1);
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
        UserAchievement userAchievement = userAchievementService.getUserAchievement(uid, 14080);
        int value = userAchievement == null ? 0 : userAchievement.getValue();
        UserAchievement yayaleAchievement = userAchievementService.getUserAchievement(uid, 13800);
        int yayaleWin = yayaleAchievement == null ? 0 : yayaleAchievement.getValue();
        UserAchievement caishuziAchievement = userAchievementService.getUserAchievement(uid, 13810);
        int caishuziWin = caishuziAchievement == null ? 0 : caishuziAchievement.getValue();
        int total = yayaleWin + caishuziWin;
        toRedis(uid, new FlxStatistic(total, total, DateUtil.getTodayInt(), caishuziWin, caishuziWin, yayaleWin,
                yayaleWin, value));
    }
}
