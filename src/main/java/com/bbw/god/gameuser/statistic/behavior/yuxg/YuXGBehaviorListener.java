package com.bbw.god.gameuser.statistic.behavior.yuxg;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.treasure.event.EPFuTuAdd;
import com.bbw.god.gameuser.treasure.event.FuTuAddEvent;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 玉虚宫行为监听器
 *
 * @author fzj
 * @date 2021/9/17 17:25
 */
@Component
@Slf4j
@Async
public class YuXGBehaviorListener {

    @Autowired
    YuXGStatisticService yuXGStatisticService;

    /**
     * 玉虚宫祈福符图获得行为监听
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void yuXGBehavior(FuTuAddEvent event) {
        try {
            EPFuTuAdd ep = event.getEP();
            if (ep.getWay() != WayEnum.YU_XG_PRAY) {
                return;
            }
            Integer futuId = ep.getId();
            List<Integer> fuTuIds = YuXGTool.getAllFuTuInfos().stream().map(CfgFuTuEntity::getFuTuId).collect(Collectors.toList());
            fuTuIds.retainAll(Arrays.asList(futuId));
            if (fuTuIds.isEmpty()) {
                return;
            }
            yuXGStatisticService.doStatistic(ep, fuTuIds);
            long uid = ep.getGuId();
            YuXGStatistic yuXGStatistic = yuXGStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), yuXGStatistic);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
