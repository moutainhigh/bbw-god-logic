package com.bbw.god.activity.holiday.processor.holidaybrocadegift;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 锦礼数据服务
 *
 * @author fzj
 * @date 2022/2/14 23:30
 */
@Service
public class HolidayBrocadeGiftService {
    /** 玩家锦礼信息 */
    private final static String BROCADE_GIFT_INFO = "brocadeGiftInfo";
    @Autowired
    private RedisHashUtil<Integer, Integer> gameBetTimesUtil;
    @Autowired
    private RedisHashUtil<String, Long> brocadeGiftNumsUtil;
    @Autowired
    private RedisHashUtil<Integer, String> lotteryNumsUtil;

    /**
     * 增加全服投注次数
     *
     * @param turn
     * @param times
     */
    public void addGameBetTimes(int type, int turn, int times) {
        String key = "game" + SPLIT + "brocadeGiftBetTimes" + SPLIT + turn;
        gameBetTimesUtil.increment(key, type, times);
        gameBetTimesUtil.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得全服投注次数
     *
     * @param turn
     * @return
     */
    public Integer getGameBetTimes(int turn, int type) {
        String key = "game" + SPLIT + "brocadeGiftBetTimes" + SPLIT + turn;
        Integer gameBetTimesUtilField = gameBetTimesUtil.getField(key, type);
        return gameBetTimesUtilField == null ? 0 : gameBetTimesUtilField;
    }

    /**
     * 更新玩家信息
     *
     * @param uid
     * @param ticketNum
     * @param userBrocadeGift
     */
    public void updateBrocadeGiftInfo(long uid, String ticketNum, UserBrocadeGift userBrocadeGift) {
        //加次数
        int newBetTimes = userBrocadeGift.getBetTimes() + 1;
        userBrocadeGift.setBetTimes(newBetTimes);
        //更新号码
        userBrocadeGift.getBetNums().add(ticketNum);
        //保存
        saveBrocadeGiftInfo(uid, userBrocadeGift);
    }

    /**
     * 保存玩家锦礼信息
     *
     * @param uid
     * @param userBrocadeGift
     */
    public void saveBrocadeGiftInfo(long uid, UserBrocadeGift userBrocadeGift) {
        List<UserBrocadeGift> userBrocadeGifts = TimeLimitCacheUtil.getFromCache(uid, BROCADE_GIFT_INFO, List.class);
        if (null == userBrocadeGifts) {
            userBrocadeGifts = new ArrayList<>();
        } else {
            userBrocadeGifts.removeIf(u -> userBrocadeGift.getBetId().equals(u.getBetId()));
        }
        userBrocadeGifts.add(userBrocadeGift);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, BROCADE_GIFT_INFO, userBrocadeGifts, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得玩家锦礼信息
     *
     * @param uid
     * @param betId
     * @return
     */
    public UserBrocadeGift getBrocadeGiftInfo(long uid, Integer betId) {
        List<UserBrocadeGift> userBrocadeGifts = TimeLimitCacheUtil.getFromCache(uid, BROCADE_GIFT_INFO, List.class);
        if (null == userBrocadeGifts) {
            return UserBrocadeGift.getInstance(betId);
        }
        UserBrocadeGift userBrocadeGift = userBrocadeGifts.stream().filter(u -> betId.equals(u.getBetId())).findFirst().orElse(null);
        if (null == userBrocadeGift) {
            return UserBrocadeGift.getInstance(betId);
        }
        return userBrocadeGift;
    }

    /**
     * 获得所有奖券号码
     *
     * @param turn
     * @return
     */
    public Map<String, Long> getAllTicketNum(Integer turn, int type) {
        String key = "game" + SPLIT + "lanternGiftsNums" + SPLIT + turn + SPLIT + type;
        Map<String, Long> allTicketNum = brocadeGiftNumsUtil.get(key);
        if (null == allTicketNum) {
            return new HashMap<>();
        }
        return allTicketNum;
    }


    /**
     * 保存奖券号码
     *
     * @param uid
     * @param ticketNum
     */
    public void saveTicketNum(long uid, String ticketNum, Integer turn, int type) {
        String key = "game" + SPLIT + "lanternGiftsNums" + SPLIT + turn + SPLIT + type;
        brocadeGiftNumsUtil.putField(key, ticketNum, uid, DateUtil.SECOND_ONE_DAY * 15);

    }

    /**
     * 保存中奖号码
     *
     * @param turn
     * @param type
     * @param lotteryNum
     */
    public void saveLotteryNum(int turn, int type, String lotteryNum) {
        String key = "game" + SPLIT + "lotteryNums" + SPLIT + turn;
        lotteryNumsUtil.putField(key, type, lotteryNum, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 根据轮次获得中奖号码
     *
     * @param turn
     * @return
     */
    public String getAllLotteryNums(int turn, int type) {
        String key = "game" + SPLIT + "lotteryNums" + SPLIT + turn;
        String lotteryNums = lotteryNumsUtil.getField(key, type);
        return lotteryNums == null ? "" : lotteryNums;
    }
}
