package com.bbw.god.statistics.userstatistic;

import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.pay.DeliverNotifyEvent;
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
public class UserRechargeCountListener {
    @Autowired
    private UserStatisticService userStatisticService;

    @Autowired
    private GameUserService gameUserService;

    private static final Integer MAX_COUNT = 328;

    @EventListener
    @Order(2)
    public void addGold(DeliverNotifyEvent event) {
        UserReceipt receipt = (UserReceipt) event.getSource();
        Long uid = receipt.getGameUserId();
        Integer totalNum = gameUserService.getMultiItems(uid, UserReceipt.class).stream().mapToInt(s -> s.getPrice()).sum();
        if (totalNum >= MAX_COUNT) {
            userStatisticService.addRMBUser(uid);
        }
    }
}
