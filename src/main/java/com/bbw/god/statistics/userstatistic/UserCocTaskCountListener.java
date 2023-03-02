package com.bbw.god.statistics.userstatistic;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



/**
 * @author suchaobin
 * @title: UserCocTaskCountListener
 * @projectName bbw-god-logic-server
 * @description:
 * @date 2019/7/421:16
 */

@Component
@Async
public class UserCocTaskCountListener {
    @Autowired
    private UserStatisticService userStatisticService;

    //商会任务完成事件监听
    @EventListener
    @Order(2)
    public void sellSpecials(CocTaskFinishedEvent event) {
        EPTaskFinished evTaskFinished = event.getEP();
        userStatisticService.addCocTask(evTaskFinished);
    }

}
