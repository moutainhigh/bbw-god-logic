package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.god.activity.holiday.lottery.HolidayLotteryParam;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryInfo;

/**
 * @author suchaobin
 * @description 可刷新的节日活动抽奖service
 * @date 2020/9/17 10:31
 **/
public interface RefreshableHolidayLotteryService extends HolidayLotteryService {
    /**
     * 奖品刷新检查
     *
     * @param uid          玩家id
     */
    void checkForRefresh(long uid, HolidayLotteryParam param);

    /**
     * 刷新奖励
     *
     * @param uid          玩家id
     */
    RDHolidayLotteryInfo refreshAwards(long uid, HolidayLotteryParam param);
}
