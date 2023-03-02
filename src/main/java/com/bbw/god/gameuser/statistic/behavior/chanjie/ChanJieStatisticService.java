package com.bbw.god.gameuser.statistic.behavior.chanjie;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.chanjie.ChanjieTools;
import com.bbw.god.game.chanjie.ChanjieType;
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
 * @description 阐截斗法统计service
 * @date 2020/4/22 11:08
 */
@Service
public class ChanJieStatisticService extends BehaviorStatisticService {
    @Autowired
    private UserAchievementService userAchievementService;

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.CHAN_JIE_FIGHT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof ChanJieStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        ChanJieStatistic chanJieStatistic = (ChanJieStatistic) statistic;
        Integer date = chanJieStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(13);
        map.put(date + UNDERLINE + NUM, chanJieStatistic.getToday());
        map.put(TOTAL, chanJieStatistic.getTotal());
        map.put(CHAN_WIN, chanJieStatistic.getChanWin());
        map.put(JIE_WIN, chanJieStatistic.getJieWin());
        map.put(DEFEAT_IMMORTAL, chanJieStatistic.getDefeatImmortal());
        map.put(DEFEAT_MASTER, chanJieStatistic.getDefeatMaster());
        map.put(CONTINUOUS_SELECT_CHAN, chanJieStatistic.getContinuousSelectChan());
        map.put(CONTINUOUS_SELECT_JIE, chanJieStatistic.getContinuousSelectJie());
        map.put(CONTINUOUS_HJFW, chanJieStatistic.getContinuousHJFW());
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
        return (T) getChanJieStatistic(date, redisMap);
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
    public ChanJieStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getChanJieStatistic(date, redisMap);
    }

    private ChanJieStatistic getChanJieStatistic(int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer chanWin = redisMap.get(CHAN_WIN) == null ? 0 : redisMap.get(CHAN_WIN);
        Integer jieWin = redisMap.get(JIE_WIN) == null ? 0 : redisMap.get(JIE_WIN);
        Integer defeatImmortal = redisMap.get(DEFEAT_IMMORTAL) == null ? 0 : redisMap.get(DEFEAT_IMMORTAL);
        Integer defeatMaster = redisMap.get(DEFEAT_MASTER) == null ? 0 : redisMap.get(DEFEAT_MASTER);
        Integer selectChan = redisMap.get(CONTINUOUS_SELECT_CHAN) == null ? 0 : redisMap.get(CONTINUOUS_SELECT_CHAN);
        Integer selectJie = redisMap.get(CONTINUOUS_SELECT_JIE) == null ? 0 : redisMap.get(CONTINUOUS_SELECT_JIE);
        Integer hjfw = redisMap.get(CONTINUOUS_HJFW) == null ? 0 : redisMap.get(CONTINUOUS_HJFW);
        return new ChanJieStatistic(today, total, date, chanWin, jieWin, defeatImmortal, defeatMaster, selectChan,
                selectJie, hjfw);
    }

    public void defeatOpponent(long uid, int date, int religiousId, int headLv) {
        ChanJieStatistic statistic = fromRedis(uid, StatisticTypeEnum.NONE, date);
        statistic.setToday(statistic.getToday() + 1);
        statistic.setTotal(statistic.getTotal() + 1);
        if (ChanjieType.Religious_CHAN.getValue().intValue() == religiousId) {
            statistic.setJieWin(statistic.getJieWin() + 1);
        } else {
            statistic.setChanWin(statistic.getChanWin() + 1);
        }
        if (ChanjieTools.FIGHT_XIAN_REN_LV <= headLv) {
            // 击杀仙人
            statistic.setDefeatImmortal(statistic.getDefeatImmortal() + 1);
        }
        if (ChanjieTools.FIGHT_LEADER_LV == headLv) {
            // 击败掌教师尊
            statistic.setDefeatMaster(statistic.getDefeatMaster() + 1);
        }
        toRedis(uid, statistic);
    }

    public void loseOpponent(long uid, int date) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, date + UNDERLINE + NUM, 1);
        redisHashUtil.increment(key, TOTAL, 1);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void selectReligious(long uid, int date, int religiousId) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        if (ChanjieType.Religious_CHAN.getValue().intValue() == religiousId) {
            redisHashUtil.increment(key, CONTINUOUS_SELECT_CHAN, 1);
        } else {
            redisHashUtil.increment(key, CONTINUOUS_SELECT_JIE, 1);
        }
        redisHashUtil.putField(key, CONTINUOUS_HJFW, 0);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    public void becomeHjfw(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.increment(key, CONTINUOUS_HJFW, 1);
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
        UserAchievement defeatJieAchivement = userAchievementService.getUserAchievement(uid, 11540);
        int defeatJie = defeatJieAchivement == null ? 0 : defeatJieAchivement.getValue();
        UserAchievement defeatChanAchievement = userAchievementService.getUserAchievement(uid, 11340);
        int defeatChan = defeatChanAchievement == null ? 0 : defeatChanAchievement.getValue();
        UserAchievement defeatImmortalAchievement = userAchievementService.getUserAchievement(uid, 11740);
        int defeatImmortal = defeatImmortalAchievement == null ? 0 : defeatImmortalAchievement.getValue();
        UserAchievement defeatMasterAchievement = userAchievementService.getUserAchievement(uid, 11940);
        int defeatMaster = defeatMasterAchievement == null ? 0 : defeatMasterAchievement.getValue();
        UserAchievement SelectChanAchievement = userAchievementService.getUserAchievement(uid, 13020);
        int SelectChan = SelectChanAchievement == null ? 0 : SelectChanAchievement.getValue();
        UserAchievement SelectJieAchievement = userAchievementService.getUserAchievement(uid, 13010);
        int SelectJie = SelectJieAchievement == null ? 0 : SelectJieAchievement.getValue();
        UserAchievement continuousHJFWAchievement = userAchievementService.getUserAchievement(uid, 13100);
        int continuousHJFW = continuousHJFWAchievement == null ? 0 : continuousHJFWAchievement.getValue();
        int total = defeatJie + defeatChan;
        toRedis(uid, new ChanJieStatistic(total, total, DateUtil.getTodayInt(), defeatJie, defeatChan, defeatImmortal,
                defeatMaster, SelectChan, SelectJie, continuousHJFW));
    }
}
