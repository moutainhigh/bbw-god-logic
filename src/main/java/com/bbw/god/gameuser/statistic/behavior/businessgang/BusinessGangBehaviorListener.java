package com.bbw.god.gameuser.statistic.behavior.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessNpcTypeEnum;
import com.bbw.god.gameuser.businessgang.cfg.CfgNpcInfo;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.digfortreasure.event.DigTreasureEvent;
import com.bbw.god.gameuser.businessgang.digfortreasure.event.EPDigTreasure;
import com.bbw.god.gameuser.businessgang.event.AddGangNpcFavorabilityEvent;
import com.bbw.god.gameuser.businessgang.event.EPAddGangNpcFavorability;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.Transmigration.TransmigrationStatistic;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.treasure.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮行为监听器
 *
 * @author fzj
 * @date 2022/2/7 21:03
 */
@Component
@Slf4j
@Async
public class BusinessGangBehaviorListener {
    /** 声望 */
    private final static List<Integer> PRESTIGE = BusinessGangCfgTool.getAllPrestigeEntity()
            .stream().map(CfgPrestigeEntity::getPrestigeId).collect(Collectors.toList());

    @Autowired
    BusinessGangStatisticService businessGangStatisticService;

    /**
     * 使用令牌监听
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void useGangToken(TreasureDeductEvent event) {
        try {
            EPTreasureDeduct ep = event.getEP();
            EVTreasure deductTreasure = ep.getDeductTreasure();
            if (deductTreasure.getId() != TreasureEnum.SHLP.getValue()) {
                return;
            }
            long uid = ep.getGuId();
            businessGangStatisticService.doStatistic(uid, deductTreasure.getNum());
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 增加声望统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void addGangPrestige(TreasureAddEvent event) {
        try {
            EPTreasureAdd ep = event.getEP();
            List<EVTreasure> addTreasures = ep.getAddTreasures();
            //判断是否添加声望
            List<EVTreasure> prestige = addTreasures.stream().filter(t -> PRESTIGE.contains(t.getId())).collect(Collectors.toList());
            if (prestige.isEmpty()) {
                return;
            }
            //执行统计
            long uid = ep.getGuId();
            businessGangStatisticService.doAddPrestigeStatistic(uid, prestige);
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 扣除声望
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void delGangPrestige(TreasureDeductEvent event) {
        try {
            EPTreasureDeduct ep = event.getEP();
            EVTreasure deductTreasure = ep.getDeductTreasure();
            //判断是否扣除声望
            boolean isPrestige = PRESTIGE.contains(deductTreasure.getId());
            if (!isPrestige) {
                return;
            }
            //执行统计
            long uid = ep.getGuId();
            businessGangStatisticService.doDelPrestigeStatistic(uid, deductTreasure);
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 完成商帮任务统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void finishGangTask(BusinessGangTaskAchievedEvent event) {
        try {
            EPBusinessGangTask ep = event.getEP();
            //执行统计
            long uid = ep.getGuId();
            businessGangStatisticService.doFinishGangTaskStatistic(ep);
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * npc增加好感度统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void addGangNpcFavorability(AddGangNpcFavorabilityEvent event) {
        try {
            EPAddGangNpcFavorability ep = event.getEP();
            int npcId = ep.getNpcId();
            CfgNpcInfo cfgNpcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
            if (cfgNpcInfo.getType() != BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
                return;
            }
            //执行统计
            long uid = ep.getGuId();
            businessGangStatisticService.doAddGangNpcFavorabilityStatistic(ep);
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 挖宝统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void digTreasure(DigTreasureEvent event) {
        try {
            EPDigTreasure ep = event.getEP();
            //执行统计
            long uid = ep.getGuId();
            businessGangStatisticService.doDigTreasureStatistic(uid);
            BusinessGangStatistic businessGangStatistic = businessGangStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), businessGangStatistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
