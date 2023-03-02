package com.bbw.god.gameuser.statistic.behavior.building;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.event.EPCardExpAdd;
import com.bbw.god.gameuser.card.event.UserCardExpAddEvent;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 领取建筑物奖励行为监听
 * @date 2020/11/24 21:05
 */
@Component
@Slf4j
@Async
public class BuildingAwardListener {
    @Autowired
    private BuildingAwardStatisticService statisticService;

    @Order(2)
    @EventListener
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        switch (way) {
            case JXZ_AWARD:
                statisticService.addJxz(ep.getGuId(), DateUtil.getTodayInt());
                break;
            case LBL_AWARD:
                statisticService.addLbl(ep.getGuId(), DateUtil.getTodayInt());
                break;
            default:
                return;
        }
        BuildingAwardStatistic statistic = statisticService.fromRedis(ep.getGuId(),
                StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
    }

    @Order(2)
    @EventListener
    public void addEle(EleAddEvent event) {
        EPEleAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        if (WayEnum.KC_AWARD == way) {
            statisticService.addKc(ep.getGuId(), DateUtil.getTodayInt());
            BuildingAwardStatistic statistic = statisticService.fromRedis(ep.getGuId(),
                    StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        }
    }

    @Order(2)
    @EventListener
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        if (WayEnum.QZ_AWARD == way) {
            statisticService.addQz(ep.getGuId(), DateUtil.getTodayInt());
            BuildingAwardStatistic statistic = statisticService.fromRedis(ep.getGuId(),
                    StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        }
    }

    @Order(2)
    @EventListener
    public void addCardExp(UserCardExpAddEvent event) {
        EPCardExpAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        if (WayEnum.LDF_AWARD == way) {
            statisticService.addLdf(ep.getGuId(), DateUtil.getTodayInt());
            BuildingAwardStatistic statistic = statisticService.fromRedis(ep.getGuId(),
                    StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        }
    }
}
