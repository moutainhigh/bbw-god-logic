package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.activity.holiday.lottery.service.HolidayLotteryServiceFactory;
import com.bbw.god.activity.holiday.lottery.service.bocake.HolidayBoCakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博饼王中王管理接口
 *
 * @author: huanghb
 * @date: 2022/1/28 14:40
 */
@RestController
@RequestMapping("/gm")
public class GMBoCakeCtrl {
    @Autowired
    private HolidayLotteryServiceFactory holidayLotteryServiceFactory;


    /**
     * 发送博饼王中王奖励
     *
     * @param groupId 区服组id
     * @return
     */
    @RequestMapping("boCake!drawWangZhongWang")
    public Rst drawWangZhongWang(Integer groupId, Integer lotteryType) {
        //如果没有传类别
        if (null == lotteryType) {
            return Rst.businessFAIL("活动类别不能为空");
        }
        //获取对应服务类
        HolidayBoCakeService service = holidayLotteryServiceFactory.getBoCakeServiceById(lotteryType);
        //王中王开奖
        service.drawWangZhongWang(groupId);

        return Rst.businessOK();

    }
}
