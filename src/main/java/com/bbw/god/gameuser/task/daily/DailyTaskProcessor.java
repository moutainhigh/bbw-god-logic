package com.bbw.god.gameuser.task.daily;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.daily.service.BaseDailyTaskService;
import com.bbw.god.gameuser.task.daily.service.DailyTaskServiceFactory;
import com.bbw.god.gameuser.task.daily.service.UserDailyTaskService;
import com.bbw.god.gameuser.task.fshelper.FsHelperService;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.random.box.BoxEventPublish;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description 每日任务
 * @date 2019-11-20 13:50
 **/
@Service
@Slf4j
public class DailyTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserDailyTaskService dailyTaskService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private FsHelperService fsHelperService;
    @Autowired
    private DailyTaskServiceFactory serviceFactory;

    private DailyTaskProcessor() {
        this.taskTypes = Collections.singletonList(TaskTypeEnum.DAILY_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo info = this.dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == info) {
            return rd;
        }
        List<RDTaskItem> rdTaskItems = getRdDailyTasks(user, info);
        rd.setItems(rdTaskItems);
        Long countdownTimes = DateUtil.millisecondsInterval(DateUtil.getDateEnd(new Date()), new Date());
        rd.setCountdownTimes(countdownTimes);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int taskId, String awardIndex) {
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo info = this.dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == info) {
            throw new ExceptionForClientTip("task.daily.already.updated");
        }
        TaskStatusEnum status = info.gainStatus(taskId);
        if (status == TaskStatusEnum.DOING) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        if (status == TaskStatusEnum.AWARDED) {
            throw new ExceptionForClientTip("task.already.award");
        }
        RDCommon rd = new RDCommon();
        if (TaskTool.isBoxTask(taskId)) {
            //宝箱
            CfgBox box = TaskTool.getDailyTaskCfgBox(taskId);
            this.boxService.open(uid, box.getBoxKey(), WayEnum.OPEN_DAILY_TASK_BOX, rd);
            BoxEventPublish.pubOpenBoxEvent(taskId, box.getScore(), uid, WayEnum.OPEN_DAILY_TASK_BOX, rd);
        } else {
            //任务
            CfgTaskEntity task = TaskTool.getDailyTask(taskId);
            this.awardService.fetchAward(uid, task.getAwards(), WayEnum.DAILY_TASK, "通过每日任务获得", rd);
            EpFsHelperChange dta = EpFsHelperChange.instanceDelTask(new BaseEventParam(uid), FsTaskEnum.Daily, taskId);
            TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
        }
        info.awarded(taskId);
        this.gameUserService.updateItem(info);
        return rd;
    }

    @Override
    public RDCommon gainBatchTaskAward(long uid, TaskTypeEnum type) {
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo taskInfo = this.dailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            throw new ExceptionForClientTip("task.daily.already.updated");
        }
        if (ListUtil.isEmpty(taskInfo.getAccomplishIds())) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        List<Integer> accomplishIds = taskInfo.getAccomplishIds().stream()
                .filter(tmp -> !TaskTool.isBoxTask(tmp))
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(accomplishIds)) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        // 更新状态
        taskInfo.getAwardedIds().addAll(accomplishIds);
        taskInfo.getAccomplishIds().removeAll(accomplishIds);
        gameUserService.updateItem(taskInfo);

        //发放奖励
        List<Award> awards = new ArrayList<>();
        for (Integer accomplishId : accomplishIds) {
            awards.addAll(TaskTool.getDailyTask(accomplishId).getAwards());
        }
        RDCommon rd = new RDCommon();
        this.awardService.fetchAward(uid, awards, WayEnum.DAILY_TASK, "通过每日任务获得", rd);

        //发布封神助手变化通知
        for (Integer accomplishId : accomplishIds) {
            EpFsHelperChange dta = EpFsHelperChange.instanceDelTask(new BaseEventParam(uid), FsTaskEnum.Daily, accomplishId);
            TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
        }
        return rd;
    }

    /**
     * 获取用户返回给客户端的任务数据
     *
     * @param user
     * @param info
     * @return
     */
    private List<RDTaskItem> getRdDailyTasks(GameUser user, UserDailyTaskInfo info) {
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        // 未解锁的情况下直接返回
        if (null == info) {
            return rdTaskItems;
        }
//        List<Integer> unFinishIds = info.getUnFinishIds();
//        if (null == unFinishIds) {
//            unFinishIds = new ArrayList<>();
//        }
        // 可能存在箱子状态异常，做修复处理
//        try {
//            if (ListUtil.isNotEmpty(unFinishIds)) {
//                List<Integer> list = ListUtil.copyList(unFinishIds, Integer.class);
//                for (Integer unFinishId : list) {
//                    if (TaskTool.isBoxTask(unFinishId)) {
//                        BaseDailyTaskService dailyTaskService = serviceFactory.getById(unFinishId);
//                        if (dailyTaskService.isAccomplished(unFinishId, info)) {
//                            continue;
//                        }
//                        int value = dailyTaskService.getMyProgress(uid, user.getLevel(), unFinishId, info);
//                        dailyTaskService.finish(user, unFinishId, value, info);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("每日任务异常的玩家id={},info={}", uid, info.toString());
//            log.error(e.getMessage(), e);
//            System.err.println("aaaaaaaaa");
//        }
        // 封装已完成数据
        List<Integer> accomplishIds = ListUtil.copyList(info.getAccomplishIds(), Integer.class);
        for (Integer accomplishId : accomplishIds) {
            rdTaskItems.add(getRdTask(user, accomplishId, info));
        }
        // 封装已领取数据
        List<Integer> awardedIds = ListUtil.copyList(info.getAwardedIds(), Integer.class);
        for (Integer awardedId : awardedIds) {
            rdTaskItems.add(getRdTask(user, awardedId, info));
        }
        List<Integer> list = ListUtil.copyList(info.getUnFinishIds(), Integer.class);
        for (Integer unFinishId : list) {
            rdTaskItems.add(getRdTask(user, unFinishId, info));
        }
        // 按id排序，和客户端约定箱子的数据放最后面
        rdTaskItems = rdTaskItems.stream().sorted(Comparator.comparing(RDTaskItem::getId)).collect(Collectors.toList());
        //移除两个商会有关任务
        rdTaskItems.removeIf(t -> t.getId() == 22020 || t.getId() == 23020);
        return rdTaskItems;
    }

    public RDTaskItem getRdTask(GameUser user, int taskId, UserTaskInfo info) {
        if (null == info) {
            return null;
        }
        long uid = user.getId();
        BaseDailyTaskService service = serviceFactory.getById(taskId);
        RDTaskItem rdt = new RDTaskItem();
        int needValue = service.getMyNeedValue(user.getLevel(), taskId);
        // 不是箱子任务
        if (!TaskTool.isBoxTask(taskId)) {
            CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(taskId);
            // value为null的是铜钱收益任务
            if (null == cfgTaskEntity.getValue()) {
                String[] strArr = {String.valueOf(needValue / 10000)};
                rdt.setTitleFormats(strArr);
            }
        }
        rdt.setId(taskId);
        rdt.setStatus(info.gainStatus(taskId).getValue());
        rdt.setProgress(service.getMyProgress(uid, user.getLevel(), taskId, info));
        rdt.setTotalProgress(needValue);
        if (TaskStatusEnum.DOING.getValue() == rdt.getStatus() &&
                rdt.getProgress() >= rdt.getTotalProgress()) {
            service.finish(user, taskId, needValue, info);
        }
        rdt.setAwards(service.getMyAwards(taskId));
        int inFsHelper = fsHelperService.existTask(FsTaskEnum.Daily, rdt.getId(), uid) ? 1 : 0;
        rdt.setInFsHelper(inFsHelper);
        return rdt;
    }


}
