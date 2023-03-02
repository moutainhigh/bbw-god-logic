package com.bbw.god.gameuser.task.daily;

import com.bbw.common.ListUtil;
import com.bbw.god.city.mixd.EPOutMxd;
import com.bbw.god.city.mixd.OutMxdEvent;
import com.bbw.god.city.mixd.event.IntoNightmareMxdEvent;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.EPCardExpAdd;
import com.bbw.god.gameuser.card.event.UserCardExpAddEvent;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.event.BehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import com.bbw.god.gameuser.statistic.event.EPResourceStatistic;
import com.bbw.god.gameuser.statistic.event.ResourceStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.task.daily.event.DailyTaskAddPoint;
import com.bbw.god.gameuser.task.daily.event.DailyTaskAddPointEvent;
import com.bbw.god.gameuser.task.daily.service.*;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.server.god.AttachNewGodEvent;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Async
public class UserDailyTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserDailyTaskService dailyTaskService;
    @Autowired
    private DailyTaskServiceFactory dailyTaskServiceFactory;

    @EventListener
    @Order(1000)
    public void doResourceDailyTask(ResourceStatisticEvent event) {
        EPResourceStatistic ep = event.getEP();
        long uid = ep.getGuId();
        GameUser user = gameUserService.getGameUser(uid);
        ResourceStatistic statistic = ep.getResourceStatistic();
        AwardEnum awardEnum = statistic.getAwardEnum();
        List<ResourceDailyTaskService> services = dailyTaskServiceFactory.getByAwardEnum(awardEnum);
        UserDailyTaskInfo taskInfo = dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            return;
        }
        doDailyTask(user, services, taskInfo);
    }

    @EventListener
    @Order(1000)
    public void doBehaviorDailyTask(BehaviorStatisticEvent event) {
        EPBehaviorStatistic ep = event.getEP();
        long uid = ep.getGuId();
        GameUser user = gameUserService.getGameUser(uid);
        BehaviorStatistic statistic = ep.getBehaviorStatistic();
        BehaviorType behaviorType = statistic.getBehaviorType();
        List<BehaviorDailyTaskService> services = dailyTaskServiceFactory.getByBehaviorType(behaviorType);
        UserDailyTaskInfo taskInfo = dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            return;
        }
        doDailyTask(user, services, taskInfo);
    }

    private <T extends BaseDailyTaskService> void doDailyTask(GameUser gu, List<T> services, UserDailyTaskInfo taskInfo) {
        long uid = gu.getId();
        int level = gu.getLevel();
        // 要通知任务助手的id集合
        List<Integer> noticeTaskIds = new ArrayList<>();
        // 完成对应任务
        for (BaseDailyTaskService service : services) {
            List<Integer> taskIds = service.getMyTaskIds();
            noticeTaskIds.addAll(taskIds);
            for (Integer taskId : taskIds) {
                if (service.isAccomplished(taskId, taskInfo)) {
                    continue;
                }
                int value = service.getMyProgress(uid, level, taskId, taskInfo);
                service.finish(gu, taskId, value, taskInfo);
            }
        }
        // 通知封神助手
        noticeFsHelper(uid, taskInfo, noticeTaskIds);
    }

    /**
     * 通知封神助手
     *
     * @param uid           玩家id
     * @param taskInfo      任务对象
     * @param noticeTaskIds 通知的任务id集合
     */
    private void noticeFsHelper(long uid, UserDailyTaskInfo taskInfo, List<Integer> noticeTaskIds) {
        List<Integer> taskIds = ListUtil.copyList(noticeTaskIds, Integer.class);
        taskIds = taskIds.stream().filter(taskInfo::isTodayTask).collect(Collectors.toList());
        taskIds.removeAll(taskInfo.getAwardedIds());
        if (ListUtil.isEmpty(taskIds)) {
            return;
        }
        EpFsHelperChange dta = EpFsHelperChange.instanceUpdateTask(new BaseEventParam(uid), FsTaskEnum.Daily, 0);
        TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
    }

    @EventListener
    @Order(1000)
    public void gainTaskPoint(DailyTaskAddPointEvent event) {
        DailyTaskAddPoint ep = event.getEP();
        long uid = ep.getGuId();
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo taskInfo = dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            return;
        }
        List<Integer> unFinishIds = taskInfo.getUnFinishIds().stream().filter(TaskTool::isBoxTask).collect(Collectors.toList());
        for (Integer unFinishId : unFinishIds) {
            BaseDailyTaskService service = dailyTaskServiceFactory.getById(unFinishId);
            int value = service.getMyProgress(uid, user.getLevel(), unFinishId, taskInfo);
            service.finish(user, unFinishId, value, taskInfo);
        }
    }

    @EventListener
    @Order(1000)
    public void outMxd(OutMxdEvent event) {
        EPOutMxd ep = event.getEP();
        long uid = ep.getGuId();
        List<Integer> taskIds = Arrays.asList(21312, 22312, 23312, 24312, 25312);
        finishDailyTask(uid, 1, taskIds);
    }

    @EventListener
    @Order(1000)
    public void intoNightmareMxd(IntoNightmareMxdEvent event) {
        long uid = event.getEP().getGuId();
        List<Integer> taskIds = Arrays.asList(21312, 22312, 23312, 24312, 25312);
        finishDailyTask(uid, 1, taskIds);
    }

    @EventListener
    @Order(1000)
    public void addCardExp(UserCardExpAddEvent event) {
        EPCardExpAdd ep = event.getEP();
        if (WayEnum.LT != ep.getWay()) {
            return;
        }
        long uid = ep.getGuId();
        List<Integer> taskIds = Arrays.asList(21313, 22313, 23313, 24313, 25313);
        finishDailyTask(uid, 1, taskIds);
    }

    @EventListener
    @Order(1000)
    public void deductSpecials(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        if (WayEnum.TYF != ep.getWay()) {
            return;
        }
        long uid = ep.getGuId();
        List<Integer> taskIds = Arrays.asList(22315, 23315);
        finishDailyTask(uid, 1, taskIds);
    }

    @EventListener
    @Order(1000)
    public void meetGod(AttachNewGodEvent event) {
        EventParam<ServerGod> eventParam = (EventParam<ServerGod>) event.getSource();
        ServerGod serverGod = eventParam.getValue();
        List<Integer> taskIds = Arrays.asList(21210, 22210, 23210, 24210, 25210);
        if (serverGod.getGodId().equals(GodEnum.BBX.getValue())) {
            taskIds = Arrays.asList(21314, 22314, 23314, 24314, 25314);
        }
        long uid = eventParam.getGuId();
        finishDailyTask(uid, 1, taskIds);
    }

    /**
     * 完成商帮任务
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void doBusinessTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask ep = event.getEP();
        if (ep.getTaskGroup() == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            return;
        }
        Long uid = ep.getGuId();
        List<Integer> taskIds = Arrays.asList(22024, 23024);
        finishDailyTask(uid, 1, taskIds);
    }

    /**
     * 完成每日任务
     *
     * @param uid      玩家id
     * @param addValue 任务增加的进度
     * @param taskIds  任务id集合
     */
    private void finishDailyTask(Long uid, int addValue, List<Integer> taskIds) {
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo taskInfo = dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            return;
        }
        for (Integer taskId : taskIds) {
            BaseDailyTaskService service = dailyTaskServiceFactory.getById(taskId);
            service.finish(user, taskId, addValue, taskInfo);
        }
        // 通知封神助手
        noticeFsHelper(uid, taskInfo, taskIds);
    }
}
