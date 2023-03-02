package com.bbw.god.gameuser.statistic.behavior.fatan;

import com.bbw.common.DateUtil;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.chengc.in.event.EPUnlockFaTan;
import com.bbw.god.city.chengc.in.event.UnlockFaTanEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 法坛行为事件监听
 *
 * @author fzj
 * @date 2021/11/1 18:16
 */
@Component
@Slf4j
@Async
public class FaTanBehaviorListener {
    @Autowired
    FaTanStatistucService faTanStatistucService;

    /**
     * 法坛升级事件监听
     */
    @Order(2)
    @EventListener
    public void buildingLevelUp(BuildingLevelUpEvent event) {
        try {
            EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
            long uid = ep.getGuId();
            List<Integer> levelUpBuildings = ep.getValue().getLevelUpBuildings();
            if (!levelUpBuildings.contains(BuildingEnum.FT.getValue())) {
                return;
            }
            faTanStatistucService.doFaTanUpStatistic(uid);
            FaTanStatistic faTanStatistic = faTanStatistucService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), faTanStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 法坛解锁事件监听
     */
    @Order(2)
    @EventListener
    public void unlockFaTan(UnlockFaTanEvent event) {
        try {
            EPUnlockFaTan ep = event.getEP();
            long uid = ep.getGuId();
            faTanStatistucService.doUnlockFaTanStatistic(uid);
            FaTanStatistic faTanStatistic = faTanStatistucService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), faTanStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
