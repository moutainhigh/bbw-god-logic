package com.bbw.god.gameuser.statistic.behavior.Transmigration;

import com.bbw.common.DateUtil;
import com.bbw.god.game.transmigration.event.EPTransmigrationSuccess;
import com.bbw.god.game.transmigration.event.TransmigrationSuccessEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * 轮回世界行为监听器
 *
 * @author fzj
 * @date 2021/9/17 17:25
 */
@Component
@Slf4j
@Async
public class TransmigrationBehaviorListener {

    @Autowired
    TransmigrationStatisticService transmigrationStatisticService;
    /**
     * 轮回世界行为监听
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void TransmigrationBehavior(TransmigrationSuccessEvent event) {
        try {
            EPTransmigrationSuccess ep = event.getEP();
            transmigrationStatisticService.doStatistic(ep);
            long uid = ep.getGuId();
            TransmigrationStatistic transmigrationStatistic = transmigrationStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), transmigrationStatistic);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
