package com.bbw.god.gameuser.statistic.behavior.changeworld;

import com.bbw.common.DateUtil;
import com.bbw.god.city.chengc.ChangeWorldEvent;
import com.bbw.god.city.chengc.EPChangeWorld;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 世界跳转统计监听器
 * @date 2020/9/24 11:28
 **/
@Component
@Async
@Slf4j
public class ChangeWorldStatisticListener {
    @Autowired
    private ChangeWorldStatisticService changeWorldStatisticService;

    @Order(2)
    @EventListener
    public void changeWorld(ChangeWorldEvent event) {
        try {
            EPChangeWorld ep = event.getEP();
            Long uid = ep.getGuId();
            changeWorldStatisticService.increment(uid, DateUtil.getTodayInt(), 1);
            ChangeWorldStatistic statistic = changeWorldStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
