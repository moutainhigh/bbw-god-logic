package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.lottery.*;
import com.bbw.god.activity.holiday.lottery.event.HolidayEventPublisher;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAward;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAwards;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryInfo;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * 五气朝元
 * @description id=30的节日抽奖service
 * @date 2020/12/22 14:59
 **/
@Service
@Slf4j
public class HolidayLotteryService30 implements HolidayLotteryService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisHashUtil<Long, UserHolidayLottery30> redisHashUtil;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private RedisHashUtil<String, List<DrawResult>> gameLotteryResultRedisUtil;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    /** 缓存过期时间 */
    private static final long TIME_OUT = 7 * 24 * 60 * 60;
    /** 临时弃用轮次 */
    private static final long TMEP_DISCARD_ROUND = 1;


    /**
     * 获取当前service对应的id
     *
     * @return
     */
    @Override
    public int getMyId() {
        return HolidayLotteryType.WQCY.getValue();
    }

    /**
     * 获取节日抽奖信息
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDHolidayLotteryInfo getHolidayLotteryInfo(long uid, HolidayLotteryParam param) {
        List<RDHolidayLotteryAwards> myRecords = new ArrayList<>();
        UserHolidayLottery30 userHolidayLottery = getCurUserHolidayLottery(uid);
        List<DrawResult> records = userHolidayLottery.getRecords();
        for (DrawResult record : records) {
            List<RDHolidayLotteryAward> awards = getAwards(uid, record).stream().map(RDHolidayLotteryAward::getInstance).collect(Collectors.toList());
            myRecords.add(new RDHolidayLotteryAwards(awards, record.getResult()));
        }
        return RDHolidayLotteryInfo.getInstance(myRecords);
    }

    /**
     * 抽奖
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDCommon draw(long uid, HolidayLotteryParam param) {
        RDCommon rd = new RDCommon();
        // 检查
        checkForDraw(uid, param);
        // 扣除法宝道具资源
        deductTreasure(uid, rd);
        // 发放奖励
        List<Integer> result = ListUtil.parseStrToInts(param.getResult());
        List<Award> awards = getAwards(uid, DrawResult.getInstance(result));
        awardService.fetchAward(uid, awards, WayEnum.HOLIDAY_LOTTERY_DRAW, "", rd);
        // 添加记录
        UserHolidayLottery30 userHolidayLottery30 = getCurUserHolidayLottery(uid);
        userHolidayLottery30.addRecord(result);
        // 抽奖次数处理
        drawTimeHandle(userHolidayLottery30);
        //缓存玩家抽奖信息到redis
        toRedis(uid, userHolidayLottery30);
        //发布五气朝元抽奖事件
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        HolidayEventPublisher.pubHolidayLotteryDrawEvent(bep, HolidayLotteryType.WQCY);
        return rd;
    }

    /**
     * 抽奖次数检查和处理
     *
     * @param userHolidayLottery30
     */
    private void drawTimeHandle(UserHolidayLottery30 userHolidayLottery30) {
        //获得玩家uid
        long uid = userHolidayLottery30.getGameUserId();
        //获得当前轮次
        int currentRound = getGameLotteryCurrentRound(uid);
        //获得每轮抽奖次数上限
        int perRoundDrawTimesLimit = HolidayWQCYTool.getCurrentDrawRoundInfos(currentRound).getPerRoundDrawTimesLimit();
        //获得玩家抽奖次数
        Integer userDrawTimes = userHolidayLottery30.getRecords().size();
        if (perRoundDrawTimesLimit != userDrawTimes) {
            return;
        }
        //玩家抽奖次数达到上限,清空奖励次数记录
        userHolidayLottery30.setRecords(new ArrayList<>());
        //获得下个轮次
        Integer nextRound = ++currentRound;
        //更新当前轮次到本地和redis
        addGameLottrtyCurrentRound(uid, nextRound);
    }

    /**
     * 获取奖励
     *
     * @param uid
     * @param result
     * @return
     */
    public List<Award> getAwards(long uid, DrawResult result) {
        List<DrawResult> holidayResults = getCurGameHolidayResult(uid);
        DrawResult holidayResult = holidayResults.stream().filter(tmp -> tmp.getResult().equals(result.getResult())).findFirst().orElse(null);
        if (null == holidayResult) {
            throw new ExceptionForClientTip("当前排序结果:" + result.toString() + "不存在");
        }
        Integer level = holidayResults.indexOf(holidayResult);
        CfgHolidayLotteryAwards cfgHolidayLotteryAwards = HolidayLotteryTool.getByAwardLevel(getMyId(), level);
        return cfgHolidayLotteryAwards.getAwards();
    }

    /**
     * 抽奖检查
     *
     * @param uid 玩家id
     */
    @Override
    public void checkForDraw(long uid, HolidayLotteryParam param) {
        //检查需要消耗的法宝道具是否足够
        checkTreasure(uid);
        List<Integer> result = ListUtil.parseStrToInts(param.getResult());
        if (HolidayWQCYTool.getFirstRewardResult().size() != result.size()) {
            throw new ExceptionForClientTip("holiday.lottery.wqcy.error");
        }
        UserHolidayLottery30 userHolidayLottery30 = getCurUserHolidayLottery(uid);
        // 已经抽过了
        if (userHolidayLottery30.getRecords().contains(DrawResult.getInstance(result))) {
            throw new ExceptionForClientTip("holiday.lottery.wqcy.draw.repet");
        }
    }

    /**
     * 检查消耗法宝道具是否足够
     *
     * @param uid
     */
    private void checkTreasure(long uid) {
        //每次抽奖需要消耗的法宝信息集合
        List<CfgHolidayWQCY.DrawConsumption> currentRoundDarwConsumptions = HolidayWQCYTool.getCurrentRoundDarwConsumption();
        for (CfgHolidayWQCY.DrawConsumption drawConsumption : currentRoundDarwConsumptions) {
            //检查消耗法宝道具是否足够
            TreasureChecker.checkIsEnough(drawConsumption.getTreasureId(), drawConsumption.getNum(), uid);
        }
    }

    /**
     * 扣除元珠
     *
     * @param uid
     */
    private void deductTreasure(long uid, RDCommon rd) {
        //每次抽奖需要消耗的法宝信息集合
        List<CfgHolidayWQCY.DrawConsumption> currentRoundDarwConsumptions = HolidayWQCYTool.getCurrentRoundDarwConsumption();
        for (CfgHolidayWQCY.DrawConsumption drawConsumption : currentRoundDarwConsumptions) {
            //扣除法宝道具
            TreasureEventPublisher.pubTDeductEvent(uid, drawConsumption.getTreasureId(), drawConsumption.getNum(), WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        }
    }

    /**
     * 奖励预览
     *
     * @return
     */
    @Override
    public RDCommon previewAwards(HolidayLotteryParam param) {
        return null;
    }

    /**
     * 获取当前玩家节日抽奖对象
     *
     * @param uid
     * @return
     */
    public UserHolidayLottery30 getCurUserHolidayLottery(long uid) {
        UserHolidayLottery30 lottery = fromRedis(uid);
        // 双重检查，懒汉单例
        if (null == lottery) {
            return (UserHolidayLottery30) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserHolidayLottery30 userLottery = fromRedis(uid);
                if (null == userLottery) {
                    userLottery = UserHolidayLottery30.getInstance(uid);
                    toRedis(uid, userLottery);
                }
                return userLottery;
            });
        }
        return lottery;
    }

    /**
     * 从redis中获取数据并转化成BaseUserHolidayLottery对象
     *
     * @param uid
     * @return
     */
    @Override
    public UserHolidayLottery30 fromRedis(long uid) {
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        String key = getKey(sid);
        return redisHashUtil.getField(key, uid);
    }

    /**
     * 将BaseUserHolidayLottery对象保存到redis中
     *
     * @param uid                玩家id
     * @param userHolidayLottery 保存的数据对象
     */
    @Override
    public void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery) {
        if (!(userHolidayLottery instanceof UserHolidayLottery30)) {
            return;
        }
        UserHolidayLottery30 holidayLottery = (UserHolidayLottery30) userHolidayLottery;
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        String key = getKey(sid);
        redisHashUtil.putField(key, uid, holidayLottery, TIME_OUT);
    }


    @Data
    @NoArgsConstructor
    public static class HolidayResult {
        // 珠子排序集合
        private List<Integer> result;

        public static HolidayResult getInstance(List<Integer> result) {
            HolidayResult instance = new HolidayResult();
            instance.setResult(result);
            return instance;
        }
    }

    /**
     * 获得节日抽奖结果key
     *
     * @param currentRound
     * @return
     */
    private String getGameHolidayResultKey(int currentRound) {
        //获得当前轮次信息
        CfgHolidayWQCY.DrawRoundInfo currentDrawRoundInfos = HolidayWQCYTool.getCurrentDrawRoundInfos(currentRound);
        return "game" + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + getMyId() + SPLIT + currentDrawRoundInfos.getRoundId();
    }

    /**
     * 获得节日抽奖轮次key
     *
     * @param uid
     * @return
     */
    public String getGameHolidayCurrentRoundKey(long uid) {
        return "game" + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + getMyId() + SPLIT + "currentRound" + SPLIT + uid;
    }

    /**
     * 保存节日抽奖结果
     *
     * @param uid
     * @param result
     */
    private void saveGameHolidayResult(long uid, List<DrawResult> result) {
        int currentRound = getGameLotteryCurrentRound(uid);
        String key = getGameHolidayResultKey(currentRound);
        gameLotteryResultRedisUtil.putField(key, key, result);
    }

    /**
     * 获取当前全服节日抽奖结果
     *
     * @param uid
     * @return
     */
    public List<DrawResult> getCurGameHolidayResult(long uid) {
        List<DrawResult> result = getGameHolidayResult(uid);
        if (ListUtil.isNotEmpty(result)) {
            return result;
        }
        result = (List<DrawResult>) redisLockUtil.doSafe(String.valueOf(uid), tmp -> {
            List<DrawResult> gameHolidayResult = getGameHolidayResult(uid);
            if (ListUtil.isNotEmpty(gameHolidayResult)) {
                return gameHolidayResult;
            }
            gameHolidayResult = CfgHolidayWQCY.rewardResults;
            Collections.shuffle(gameHolidayResult);
            saveGameHolidayResult(uid, gameHolidayResult);
            return gameHolidayResult;
        });
        return result;
    }

    /**
     * 获取全服节日抽奖结果
     *
     * @param uid
     * @return
     */
    private List<DrawResult> getGameHolidayResult(long uid) {
        String key = getGameHolidayResultKey(getGameLotteryCurrentRound(uid));
        if (!gameLotteryResultRedisUtil.hasField(key, key)) {
            return null;
        }
        List<DrawResult> results = gameLotteryResultRedisUtil.getField(key, key);
        return results;
    }


    /**
     * 获得玩家五气朝元抽奖当前轮次
     *
     * @param uid
     * @return
     */
    private Integer getGameLotteryCurrentRound(long uid) {
        //获得当前轮次
        Integer currentRound = getCurrentRound(uid);
        //当前轮次不为空
        if (null != currentRound && TMEP_DISCARD_ROUND != 1) {
            return currentRound;
        }
        currentRound = HolidayWQCYTool.getAllDrawRoundInfos().get(0).getMinRound();
        addGameLottrtyCurrentRound(uid, currentRound);
        return currentRound;
    }

    /**
     * 获得当前轮次
     *
     * @param uid
     * @return
     */
    private Integer getCurrentRound(long uid) {
        String currentRoundKey = getGameHolidayCurrentRoundKey(uid);
        return TimeLimitCacheUtil.getFromCache(uid, currentRoundKey, Integer.class);
    }

    /**
     * 保存当前轮次信息
     *
     * @param uid
     * @param currentRound
     */
    public void addGameLottrtyCurrentRound(long uid, Integer currentRound) {
        String currentRoundKey = getGameHolidayCurrentRoundKey(uid);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, currentRoundKey, currentRound, TIME_OUT);
    }
}
