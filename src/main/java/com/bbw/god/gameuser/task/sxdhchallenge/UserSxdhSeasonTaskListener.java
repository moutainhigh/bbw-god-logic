package com.bbw.god.gameuser.task.sxdhchallenge;

import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.sxdh.SxdhStatistic;
import com.bbw.god.gameuser.statistic.event.BehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Async
public class UserSxdhSeasonTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private UserSxdhSeasonTaskService userSxdhSeasonTaskService;

    @Order(10000)
    @EventListener
    public void fightWin(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        if (WayEnum.SXDH_FIGHT != ep.getWay()) {
            return;
        }
        BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
        if (!(behaviorStatistic instanceof SxdhStatistic)) {
            return;
        }
        long uid = ep.getGuId();
        SxdhZone zone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == zone) {
            return;
        }
        SxdhStatistic statistic = (SxdhStatistic) behaviorStatistic;
        Map<String, Integer> seasonWinMap = statistic.getSeasonWinMap();
        Map<String, Integer> seasonKillCardsMap = statistic.getSeasonKillCardsMap();
        Map<String, Integer> seasonDefeatHpMap = statistic.getSeasonDefeatHpMap();
        Map<String, Integer> seasonJoinDaysMap = statistic.getSeasonJoinDaysMap();
        Integer seasonJoinDays = seasonJoinDaysMap.get(zone.getSeason().toString()) == null ?
                0 : seasonJoinDaysMap.get(zone.getSeason().toString());
        Integer seasonDefeatHp = seasonDefeatHpMap.get(zone.getSeason().toString()) == null ?
                0 : seasonDefeatHpMap.get(zone.getSeason().toString());
        Integer seasonKillCards = seasonKillCardsMap.get(zone.getSeason().toString()) == null ?
                0 : seasonKillCardsMap.get(zone.getSeason().toString());
        Integer seasonWin = seasonWinMap.get(zone.getSeason().toString()) == null ?
                0 : seasonWinMap.get(zone.getSeason().toString());
        resetAchieveSeasonTask(uid, 8010, seasonJoinDays);
        resetAchieveSeasonTask(uid, 8020, seasonDefeatHp);
        resetAchieveSeasonTask(uid, 8030, seasonKillCards);
        resetAchieveSeasonTask(uid, 8050, seasonWin);
    }

    @Order(10000)
    @EventListener
    public void fightFail(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        if (WayEnum.SXDH_FIGHT != ep.getWay()) {
            return;
        }
        BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
        if (!(behaviorStatistic instanceof SxdhStatistic)) {
            return;
        }
        long uid = ep.getGuId();
        SxdhZone zone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == zone) {
            return;
        }
        SxdhStatistic statistic = (SxdhStatistic) behaviorStatistic;
        Map<String, Integer> seasonKillCardsMap = statistic.getSeasonKillCardsMap();
        Map<String, Integer> seasonDefeatHpMap = statistic.getSeasonDefeatHpMap();
        Map<String, Integer> seasonJoinDaysMap = statistic.getSeasonJoinDaysMap();
        Integer seasonJoinDays = seasonJoinDaysMap.get(zone.getSeason().toString()) == null ?
                0 : seasonJoinDaysMap.get(zone.getSeason().toString());
        Integer seasonDefeatHp = seasonDefeatHpMap.get(zone.getSeason().toString()) == null ?
                0 : seasonDefeatHpMap.get(zone.getSeason().toString());
        Integer seasonKillCards = seasonKillCardsMap.get(zone.getSeason().toString()) == null ?
                0 : seasonKillCardsMap.get(zone.getSeason().toString());
        resetAchieveSeasonTask(uid, 8010, seasonJoinDays);
        resetAchieveSeasonTask(uid, 8020, seasonDefeatHp);
        resetAchieveSeasonTask(uid, 8030, seasonKillCards);
    }

    @Order(10000)
    @EventListener
    public void changeCard(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        if (WayEnum.SXDH_REFRESH_CARD != ep.getWay()) {
            return;
        }
        BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
        if (!(behaviorStatistic instanceof SxdhStatistic)) {
            return;
        }
        long uid = ep.getGuId();
        SxdhZone zone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == zone) {
            return;
        }
        SxdhStatistic statistic = (SxdhStatistic) behaviorStatistic;
        Map<String, Integer> seasonChangeCardsMap = statistic.getSeasonChangeCardsMap();
        Integer seasonChangeCards = seasonChangeCardsMap.get(zone.getSeason().toString()) == null ?
                0 : seasonChangeCardsMap.get(zone.getSeason().toString());
        resetAchieveSeasonTask(uid, 8040, seasonChangeCards);
    }

    private void achieveSeasonTask(long uid, int taskId, int addValue) {
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == sxdhZone) {
            return;
        }
        UserSxdhSeasonTask seasonTask = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, taskId);
        if (null == seasonTask || seasonTask.ifAccomplished()) {
            return;
        }
        seasonTask.addValue(addValue);
        this.gameUserService.updateItem(seasonTask);
    }

    private void resetAchieveSeasonTask(long uid, int taskId, int value) {
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == sxdhZone) {
            return;
        }
        UserSxdhSeasonTask seasonTask = userSxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, taskId);
        if (null == seasonTask || seasonTask.ifAccomplished()) {
            return;
        }
        seasonTask.updateValue(value);
        if (seasonTask.ifAccomplished()) {
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.TASK, TaskTypeEnum.SXDH_SEASON_TASK.getValue(),
                    taskId);
        }
        this.gameUserService.updateItem(seasonTask);
    }
}
