package com.bbw.god.statistics.userstatistic;

import com.bbw.god.game.award.AwardEnum;
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
 */

@Component
@Async
public class UserCopperCountListener {
    @Autowired
    private UserStatisticService userStatisticService;

    private static final Integer[] MAX_COUNT = {10000 * 10000};

    @EventListener
    @Order(2)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        AwardEnum anEnum = AwardEnum.TQ;
        long addedCopper = ep.gainAddCopper();
        userStatisticService.addOutput(ep.getGuId(), ep.getWay(), addedCopper, MAX_COUNT, anEnum);
    }

    @EventListener
    @Order(2)
    public void deductCopper(CopperDeductEvent event) {
        EPCopperDeduct ep = event.getEP();
        AwardEnum anEnum = AwardEnum.TQ;
        long value = ep.getDeductCopper();
        userStatisticService.addConsume(ep.getGuId(), ep.getWay(), value, anEnum);
    }
}
