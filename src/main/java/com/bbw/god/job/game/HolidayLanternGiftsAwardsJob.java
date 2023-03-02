package com.bbw.god.job.game;

import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 锦礼开奖定时器
 *
 * @author fzj
 * @date 2022/2/12 13:46
 */
@Component("holidayLanternGiftsAwardsJob")
@Slf4j
public class HolidayLanternGiftsAwardsJob extends GameJob {
    @Autowired
    HolidayBrocadeGiftProcessor holidayLanternGiftsProcessor;

    @Override
    public String getJobDesc() {
        return "锦礼开奖";
    }

    @Override
    public void job() {
        //延迟时间
        int delayMinute = 5;
        holidayLanternGiftsProcessor.drawPrize(delayMinute);
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
