package com.bbw.god.statistics.serverstatistic;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.random.box.EPOpenBox;
import com.bbw.god.random.box.OpenBoxEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description: 区服每日任务统计
 * @date 2020/03/03 14:42
 */
@Component
@Async
public class ServerDailyTaskListener {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private GodServerStatisticService godServerStatisticService;

    @EventListener
    @Order(2)
    public void openBox(OpenBoxEvent event) {
        EPOpenBox ep = event.getEP();
        Long guId = ep.getGuId();
        GameUser gu = gameUserService.getGameUser(guId);
        godServerStatisticService.dailyTaskStatistic(gu.getServerId(), ep.getScore(), 1);
    }

}
