package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.god.activity.holiday.lottery.BaseUserHolidayLottery;
import com.bbw.god.activity.holiday.lottery.HolidayLotteryParam;
import com.bbw.god.rd.RDCommon;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 节日抽奖service
 * @date 2020/9/17 10:06
 **/
public interface HolidayLotteryService {
    /**
     * 获取当前service对应的id
     *
     * @return
     */
    int getMyId();

    /**
     * 获取节日抽奖信息
     *
     * @param uid 玩家id
     * @return
     */
    RDCommon getHolidayLotteryInfo(long uid, HolidayLotteryParam param);

    /**
     * 抽奖
     *
     * @param uid 玩家id
     * @return
     */
    RDCommon draw(long uid, HolidayLotteryParam param);

    /**
     * 抽奖检查
     *
     * @param uid 玩家id
     */
    void checkForDraw(long uid, HolidayLotteryParam param);

    /**
     * 奖励预览
     *
     * @return
     */
    RDCommon previewAwards(HolidayLotteryParam param);

    /**
     * 获取保存到redis中的key
     *
     * @param sid 区服id
     * @return
     */
    default String getKey(int sid) {
        return "server" + SPLIT + sid + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + getMyId();
    }

    /**
     * 从redis中获取数据并转化成BaseUserHolidayLottery对象
     *
     * @param uid
     * @return
     */
    BaseUserHolidayLottery fromRedis(long uid);

    /**
     * 将BaseUserHolidayLottery对象保存到redis中
     *
     * @param uid                玩家id
     * @param userHolidayLottery 保存的数据对象
     */
    void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery);
}
