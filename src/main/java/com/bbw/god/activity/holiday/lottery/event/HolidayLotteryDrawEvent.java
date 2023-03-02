package com.bbw.god.activity.holiday.lottery.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 节日抽奖事件
 * @date 2020/8/28 13:46
 **/
public class HolidayLotteryDrawEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1488004469919304714L;

    public HolidayLotteryDrawEvent(EPHolidayLotteryDraw source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public EPHolidayLotteryDraw getEP() {
        return (EPHolidayLotteryDraw) getSource();
    }
}
