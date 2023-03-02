package com.bbw.god.gm.service;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 超级锦礼管理接口
 *
 * @author: huanghb
 * @date: 2022/1/28 14:40
 */
@RestController
@RequestMapping("/gm")
public class GMJinLi {
    @Autowired
    private HolidayBrocadeGiftProcessor holidayBrocadeGiftProcessor;

    /**
     * 超级锦礼补发奖励
     *
     * @param
     * @return
     */
    @RequestMapping("jinLi!draw")
    public Rst draw(String turnDate) {
        Date date = DateUtil.fromDateTimeString(turnDate);
        holidayBrocadeGiftProcessor.drawPrize(date);
        return Rst.businessOK();
    }
}
