package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.holiday.lottery.service.bocake.HolidayBoCakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 节日抽奖工厂
 * @date 2020/9/17 11:13
 **/
@Service
public class HolidayLotteryServiceFactory {
    @Autowired
    @Lazy
    private List<HolidayLotteryService> holidayLotteryServices;

    @Autowired
    @Lazy
    private List<RefreshableHolidayLotteryService> refreshableHolidayLotteryServices;

    @Autowired
    @Lazy
    private List<HolidayBoCakeService> holidayBoCakeServices;

    public HolidayLotteryService getBaseServiceById(int id) {
        for (HolidayLotteryService service : holidayLotteryServices) {
            if (service.getMyId() == id) {
                return service;
            }
        }
        throw CoderException.high(String.format("程序员没有编写id=%s的节日抽奖service", id));
    }

    public RefreshableHolidayLotteryService getRefreshableServiceById(int id) {
        for (RefreshableHolidayLotteryService service : refreshableHolidayLotteryServices) {
            if (service.getMyId() == id) {
                return service;
            }
        }
        throw CoderException.high(String.format("程序员没有编写id=%s的可刷新的节日抽奖service", id));
    }

    public HolidayBoCakeService getBoCakeServiceById(int id) {
        for (HolidayBoCakeService service : holidayBoCakeServices) {
            if (service.getMyId() == id) {
                return service;
            }
        }
        throw CoderException.high(String.format("程序员没有编写id=%s的博饼service", id));
    }
}
