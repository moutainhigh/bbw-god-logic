package com.bbw.god.activity.holiday.lottery.service.bocake;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.activity.holiday.lottery.BaseUserHolidayLottery;
import com.bbw.god.activity.holiday.lottery.UserHolidayBoCake;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 博饼类抽奖redis对应service
 *
 * @author: huanghb
 * @date: 2022/1/11 15:19
 */
@Service
public class HolidayBoCakeRedisService {
    @Autowired
    private RedisZSetUtil<String> redisZSetUtil;
    @Autowired
    private RedisHashUtil<String, Long> zhuangYuanRedisUtil;
    @Autowired
    private RedisHashUtil<String, List<String>> wangZhongWangRedisUtil;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisHashUtil<Long, UserHolidayBoCake> redisHashUtil;
    /** 缓存超时时间 */
    protected final static long CACHE_TIME_OUT = 10;

    /**
     * 获得状元信息
     *
     * @param groupId
     * @param lotteryType
     * @return
     */
    protected Map<String, Long> getZhuangYuanMap(int groupId, int lotteryType) {
        String key = HolidayBoCakeRedisKey.getZhuangYuanKey(groupId, lotteryType);
        return zhuangYuanRedisUtil.get(key);
    }

    /**
     * 缓存王中王信息到redis
     *
     * @param groupId
     * @param wzw
     */
    protected void setWangZhongWangToRedis(int groupId, int lotteryType, WangZhongWang wzw) {
        Map<String, List<String>> redisMap = new HashMap<>(6);
        redisMap.put("firstPrize", wzw.getFirstPrize());
        redisMap.put("secondPrize", wzw.getSecondPrize());
        redisMap.put("thirdPrize", wzw.getThirdPrize());
        redisMap.put("fourthPrize", wzw.getFourthPrize());
        String cacheKey = HolidayBoCakeRedisKey.getGameWangZhongWangKey(groupId, lotteryType);
        wangZhongWangRedisUtil.putAllField(cacheKey, redisMap);
        wangZhongWangRedisUtil.expire(cacheKey, CACHE_TIME_OUT, TimeUnit.DAYS);

    }

    /**
     * 获得王中王前四记录
     *
     * @param groupId
     * @return
     */
    protected WangZhongWang getWangZhongWangfromRedis(int groupId, int lotteryType) {
        String key = HolidayBoCakeRedisKey.getGameWangZhongWangKey(groupId, lotteryType);
        Map<String, List<String>> redisMap = wangZhongWangRedisUtil.get(key);
        List<String> firstPrize = redisMap.get("firstPrize");
        List<String> secondPrize = redisMap.get("secondPrize");
        List<String> thirdPrize = redisMap.get("thirdPrize");
        List<String> fourthPrize = redisMap.get("fourthPrize");
        return new WangZhongWang(firstPrize, secondPrize, thirdPrize, fourthPrize);
    }

    /**
     * 添加游戏记录
     *
     * @param uid
     * @param resultLevel
     */
    protected void addGameRecord(long uid, int resultLevel, int lotteryType) {
        long now = DateUtil.toDateTimeLong();
        String value = uid + SPLIT + now + SPLIT + resultLevel;
        Integer groupId = gameUserService.getActiveGid(uid);
        String key = HolidayBoCakeRedisKey.getGameRecordKey(groupId, lotteryType);
        redisZSetUtil.add(key, value, now);
    }

    /**
     * 获取全服中奖纪录（最多100条）
     *
     * @param groupId 平台id
     * @return
     */
    protected List<String> getGameRecords(int groupId, int lotteryType) {
        String key = HolidayBoCakeRedisKey.getGameRecordKey(groupId, lotteryType);
        Set<String> range = redisZSetUtil.reverseRange(key, 0, 100);
        List<String> gameRecords = new ArrayList<>();
        if (SetUtil.isEmpty(range)) {
            return gameRecords;
        }
        for (String str : range) {
            String[] split = str.split(SPLIT);
            long uid = Long.parseLong(split[0]);
            ResultLevelEnum resultLevel = ResultLevelEnum.fromValue(Integer.parseInt(split[2]));
            GameUser gu = gameUserService.getGameUser(uid);
            String server = gameUserService.getOriServer(uid).getShortName();
            String msg = server + " " + gu.getRoleInfo().getNickname() + " 福星高照，喜得" + resultLevel.getName() + "!";
            gameRecords.add(msg);
        }
        return gameRecords;
    }

    /**
     * 从redis中获取数据并转化成BaseUserHolidayLottery对象
     *
     * @param uid
     * @param lotteryType
     * @return
     */
    public UserHolidayBoCake fromRedis(long uid, int lotteryType) {
        int sid = gameUserService.getActiveSid(uid);
        String key = HolidayBoCakeRedisKey.getBaseUserHolidayLotteryKey(sid, lotteryType);
        return redisHashUtil.getField(key, uid);
    }

    /**
     * 将BaseUserHolidayLottery对象保存到redis中
     *
     * @param uid                玩家id
     * @param userHolidayLottery 保存的数据对象
     */
    public void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery, int lotteryType, long cacheTimeOut) {
        if (!(userHolidayLottery instanceof UserHolidayBoCake)) {
            throw CoderException.high("参数类型错误");
        }
        UserHolidayBoCake holidayBoCake = (UserHolidayBoCake) userHolidayLottery;
        int sid = gameUserService.getActiveSid(uid);
        String key = HolidayBoCakeRedisKey.getBaseUserHolidayLotteryKey(sid, lotteryType);
        redisHashUtil.putField(key, uid, holidayBoCake, cacheTimeOut);
    }

    /**
     * 缓存状元奖券的编号
     *
     * @param uid
     * @param groupId
     * @param lotteryType
     * @param number
     * @param cacheTimeOut
     */
    protected void cacheZhuangYuanNoInfo(long uid, int groupId, int lotteryType, String number, long cacheTimeOut) {
        String key = HolidayBoCakeRedisKey.getZhuangYuanKey(groupId, lotteryType);
        zhuangYuanRedisUtil.putField(key, number, uid, cacheTimeOut);
    }
}
