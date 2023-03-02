package com.bbw.god.gameuser.statistic.behavior.task;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.task.timelimit.cunz.event.CunZTaskAchievedEvent;
import com.bbw.god.gameuser.task.timelimit.cunz.event.EPCunZTask;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 任务监听类
 * @date 2020/4/22 9:14
 */
@Component
@Slf4j
@Async
public class TaskBehaviorListener {
    @Autowired
    private CocTaskStatisticService cocTaskStatisticService;
    @Autowired
    private GuildTaskStatisticService guildTaskStatisticService;
    @Autowired
    private CunZTaskStatisticService cunZTaskStatisticService;


    @Order(2)
    @EventListener
    public void finishCocTask(CocTaskFinishedEvent event) {
        try {
            EPTaskFinished ep = event.getEP();
            Long uid = ep.getGuId();
            cocTaskStatisticService.increment(uid, DateUtil.getTodayInt(), 1);
            CocTaskStatistic statistic = cocTaskStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(2)
    @EventListener
    public void finishGuildTask(GuildTaskFinishedEvent event) {
        try {
            EPGuildTaskFinished ep = event.getEP();
            Long uid = ep.getGuId();
            guildTaskStatisticService.increment(uid, DateUtil.getTodayInt(), 1);
            GuildTaskStatistic statistic = guildTaskStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 完成村庄任务统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void finishCunZTask(CunZTaskAchievedEvent event) {
        try {
            EPCunZTask ep = event.getEP();
            Long uid = ep.getGuId();

            if (cunZTaskStatisticService.isToStatistic(ep.getTaskId())) {
                cunZTaskStatisticService.doStatistic(uid, ep.getTaskId(), ep.isFirstAchieved());
                CunZTaskStatistic statistic = cunZTaskStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
                StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
