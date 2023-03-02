package com.bbw.god.activity.holiday.lottery.event;

import com.bbw.god.activity.holiday.lottery.HolidayLotteryType;
import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 节日抽奖事件参数
 * @date 2020/8/28 13:47
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPHolidayLotteryDraw extends BaseEventParam {
    private HolidayLotteryType lotteryType;
    private Integer resultLevel;

    public EPHolidayLotteryDraw(BaseEventParam bep, HolidayLotteryType lotteryType) {
        this.lotteryType = lotteryType;
        setValues(bep);
    }

    public EPHolidayLotteryDraw(BaseEventParam bep, HolidayLotteryType lotteryType, Integer resultLevel) {
        this.lotteryType = lotteryType;
        this.resultLevel = resultLevel;
        setValues(bep);
    }
}
