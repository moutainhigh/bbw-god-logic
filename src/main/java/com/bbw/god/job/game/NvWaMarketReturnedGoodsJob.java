package com.bbw.god.job.game;

import com.bbw.god.city.nvwm.nightmare.nuwamarket.NvWaMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 女娲集市返还玩家道具定时器
 *
 * @author fzj
 * @date 2022/6/8 17:37
 */
@Slf4j
@Component("nvWaMarketTimingGoodsJob")
public class NvWaMarketReturnedGoodsJob extends GameJob {
    @Autowired
    NvWaMarketService nvWaMarketService;

    @Override
    public String getJobDesc() {
        return "女娲集市相关定时处理";
    }

    @Override
    public void job() {
        //还价返还处理
        nvWaMarketService.executeAddPendOperate(5);
        //摊位过期处理
        nvWaMarketService.handlingExpiredBooth();
        //还价过期处理
        nvWaMarketService.handlingExpiredBargain(1);
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
