package com.bbw.god.statistics.serverstatistic;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @title: ServerCopperListener
 * @projectName bbw-god-logic-server
 * @description: 区服铜钱统计
 * @date 2019/6/1814:52
 */
@Component
@Async
public class ServerCopperListener {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private GodServerStatisticService godServerStatisticService;

    @EventListener
    @Order(2)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        long addedCopper = ep.gainAddCopper();
        AwardEnum awardEnum = AwardEnum.TQ;
        int sid = gameUserService.getActiveSid(ep.getGuId());
        WayEnum way = ep.getWay();
        godServerStatisticService.addOutput(sid, way, addedCopper, awardEnum.getName());
    }

    @EventListener
    @Order(2)
    public void deductCopper(CopperDeductEvent event) {
        EPCopperDeduct ep = event.getEP();
        long value = ep.getDeductCopper();
        AwardEnum awardEnum = AwardEnum.TQ;
        int sid = gameUserService.getActiveSid(ep.getGuId());
        WayEnum way = ep.getWay();
        godServerStatisticService.addConsume(sid, way, value, awardEnum.getName());
    }
}
