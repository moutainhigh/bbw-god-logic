package com.bbw.god.gameuser.statistic.behavior.yaozu;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.yaozu.event.EPYaoZu;
import com.bbw.god.gameuser.yaozu.event.YaoZuBeatEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 妖族行为事件监听器
 *
 * @author fzj
 * @date 2021/9/8 15:50
 */
@Component
@Slf4j
@Async
public class YaoZuBehaviorListener {
    @Autowired
    public YaoZuStatisticService yaoZuStatisticService;
    /**
     * 击败妖族统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void YaoZuBeat(YaoZuBeatEvent event) {
        try {
            EPYaoZu ep = event.getEP();
            Long uid = ep.getGuId();
            yaoZuStatisticService.doStatistic(uid, ep.getYaoZuId());
            YaoZuStatistic yaoZuStatistic = yaoZuStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), yaoZuStatistic);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
