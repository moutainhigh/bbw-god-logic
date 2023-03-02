package com.bbw.god.gameuser.task.dfdjchallenge;

import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.dfdj.DfdjStatistic;
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
public class UserDfdjSeasonTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private UserDfdjSeasonTaskService userDfdjSeasonTaskService;

    @Order(10000)
    @EventListener
    public void fightWin(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        if (WayEnum.DFDJ_FIGHT != ep.getWay()) {
            return;
        }
        BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
        if (!(behaviorStatistic instanceof DfdjStatistic)) {
            return;
        }
        long uid = ep.getGuId();
        DfdjZone zone = dfdjZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == zone) {
            return;
        }
        DfdjStatistic statistic = (DfdjStatistic) behaviorStatistic;
        Map<String, Integer> seasonWinMap = statistic.getSeasonWinMap();
        Map<String, Integer> seasonKillCardsMap = statistic.getSeasonKillCardsMap();
        Map<String, Integer> seasonDefeatHpMap = statistic.getSeasonDefeatHpMap();
        Map<String, Integer> seasonJoinDaysMap = statistic.getSeasonJoinDaysMap();
        String season = zone.getSeason().toString();
        int seasonJoinDays = seasonJoinDaysMap.get(season) == null ? 0 : seasonJoinDaysMap.get(season);
        int seasonDefeatHp = seasonDefeatHpMap.get(season) == null ? 0 : seasonDefeatHpMap.get(season);
        int seasonKillCards = seasonKillCardsMap.get(season) == null ? 0 : seasonKillCardsMap.get(season);
        int seasonWin = seasonWinMap.get(season) == null ? 0 : seasonWinMap.get(season);
        resetAchieveSeasonTask(uid, 9010, seasonJoinDays);
        resetAchieveSeasonTask(uid, 9020, seasonDefeatHp);
        resetAchieveSeasonTask(uid, 9030, seasonKillCards);
        resetAchieveSeasonTask(uid, 9040, seasonWin);
    }

    @Order(10000)
    @EventListener
    public void fightFail(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        if (WayEnum.DFDJ_FIGHT != ep.getWay()) {
            return;
        }
        BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
        if (!(behaviorStatistic instanceof DfdjStatistic)) {
            return;
        }
        long uid = ep.getGuId();
        DfdjZone zone = dfdjZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == zone) {
            return;
        }
        DfdjStatistic statistic = (DfdjStatistic) behaviorStatistic;
        Map<String, Integer> seasonKillCardsMap = statistic.getSeasonKillCardsMap();
        Map<String, Integer> seasonDefeatHpMap = statistic.getSeasonDefeatHpMap();
        Map<String, Integer> seasonJoinDaysMap = statistic.getSeasonJoinDaysMap();
        String season = zone.getSeason().toString();
        int seasonJoinDays = seasonJoinDaysMap.get(season) == null ? 0 : seasonJoinDaysMap.get(season);
        int seasonDefeatHp = seasonDefeatHpMap.get(season) == null ? 0 : seasonDefeatHpMap.get(season);
        int seasonKillCards = seasonKillCardsMap.get(season) == null ? 0 : seasonKillCardsMap.get(season);
        resetAchieveSeasonTask(uid, 9010, seasonJoinDays);
        resetAchieveSeasonTask(uid, 9020, seasonDefeatHp);
        resetAchieveSeasonTask(uid, 9030, seasonKillCards);
    }

    private void resetAchieveSeasonTask(long uid, int taskId, int value) {
        DfdjZone sxdhZone = dfdjZoneService.getZoneByServer(gameUserService.getOriServer(uid));
        if (null == sxdhZone) {
            return;
        }
        UserDfdjSeasonTask seasonTask = userDfdjSeasonTaskService.getSeasonTask(uid, sxdhZone, taskId);
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
