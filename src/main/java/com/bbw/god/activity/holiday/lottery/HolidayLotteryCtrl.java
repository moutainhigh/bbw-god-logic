package com.bbw.god.activity.holiday.lottery;

import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryInfo;
import com.bbw.god.activity.holiday.lottery.service.HolidayLotteryService;
import com.bbw.god.activity.holiday.lottery.service.HolidayLotteryServiceFactory;
import com.bbw.god.activity.holiday.lottery.service.RefreshableHolidayLotteryService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 节日抽奖接口
 * @date 2020/8/27 10:30
 **/
@RestController
public class HolidayLotteryCtrl extends AbstractController {
    @Autowired
    private HolidayLotteryServiceFactory serviceFactory;

    /**
     * 进入节日抽奖界面
     *
     * @return
     */
    @GetMapping(CR.Activity.ENTER_LOTTERY)
    public RDCommon getHolidayLotteryInfo(HolidayLotteryParam param) {
        HolidayLotteryService service = serviceFactory.getBaseServiceById(param.getType());
        return service.getHolidayLotteryInfo(getUserId(), param);
    }

    /**
     * 奖励预览
     *
     * @return
     */
    @GetMapping(CR.Activity.PREVIEW_LOTTERY_AWARD)
    public RDCommon previewAwards(HolidayLotteryParam param) {
        HolidayLotteryService service = serviceFactory.getBaseServiceById(param.getType());
        return service.previewAwards(param);
    }

    /**
     * 刷新奖励
     *
     * @param param
     * @return
     */
    @GetMapping(CR.Activity.REFRESH_LOTTERY_AWARD)
    public RDHolidayLotteryInfo refreshLotteryAwards(HolidayLotteryParam param) {
        RefreshableHolidayLotteryService service = serviceFactory.getRefreshableServiceById(param.getType());
        return service.refreshAwards(getUserId(), param);
    }

    /**
     * 抽奖
     *
     * @return
     */
    @GetMapping(CR.Activity.DRAW_LOTTERY)
    public RDCommon drawHolidayLottery(HolidayLotteryParam param) {
        HolidayLotteryService service = serviceFactory.getBaseServiceById(param.getType());
        return service.draw(getUserId(), param);
    }
}
