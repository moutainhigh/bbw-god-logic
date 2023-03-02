package com.bbw.god.activity.holiday.lottery.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.activity.holiday.lottery.HolidayLotteryType;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 节日事件发布器
 * @date 2020/8/28 13:50
 **/
public class HolidayEventPublisher {

    public static void pubHolidayLotteryDrawEvent(BaseEventParam bep, HolidayLotteryType lotteryType) {
        EPHolidayLotteryDraw ep = new EPHolidayLotteryDraw(bep, lotteryType);
        SpringContextUtil.publishEvent(new HolidayLotteryDrawEvent(ep));
    }

    public static void pubHolidayLotteryDrawEvent(BaseEventParam bep, HolidayLotteryType lotteryType, Integer resultLevel) {
        EPHolidayLotteryDraw ep = new EPHolidayLotteryDraw(bep, lotteryType, resultLevel);
        SpringContextUtil.publishEvent(new HolidayLotteryDrawEvent(ep));
    }
}
