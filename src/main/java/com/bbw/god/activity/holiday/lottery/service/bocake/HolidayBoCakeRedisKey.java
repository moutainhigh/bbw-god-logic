package com.bbw.god.activity.holiday.lottery.service.bocake;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 博饼类抽奖Redis对应的key
 *
 * @author: huanghb
 * @date: 2022/1/11 10:18
 */
public class HolidayBoCakeRedisKey {
    /**
     * 获得王中王key
     *
     * @param groupId
     * @param lotteryType 抽奖类型
     * @return
     */
    protected static String getGameWangZhongWangKey(int groupId, int lotteryType) {
        if (groupId == 17) {
            groupId = 16;
        }
        return "game" + SPLIT + groupId + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + lotteryType + SPLIT + "WZW";
    }

    /**
     * 获得状元key
     *
     * @param groupId
     * @param lotteryType 抽奖类型
     * @return
     */
    protected static String getZhuangYuanKey(int groupId, int lotteryType) {
        if (groupId == 17) {
            groupId = 16;
        }
        return "game" + SPLIT + groupId + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + lotteryType + SPLIT + "ZY";
    }

    /**
     * 获得游戏记录key
     *
     * @param groupId
     * @param lotteryType 抽奖类型
     * @return
     */
    protected static String getGameRecordKey(int groupId, int lotteryType) {
        if (groupId == 17) {
            groupId = 16;
        }
        return "game" + SPLIT + groupId + SPLIT + "holiday" + SPLIT + "zqbb" + SPLIT + lotteryType + SPLIT + "record";
    }

    /**
     * 获取保存到redis中的key(BaseUserHolidayLottery对象)
     *
     * @param sid
     * @param lotteryType
     * @return
     */
    protected static String getBaseUserHolidayLotteryKey(int sid, int lotteryType) {
        return "server" + SPLIT + sid + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + lotteryType;
    }
}
