package com.bbw.god.gameuser.task.activitytask;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 活动每日任务service
 *
 * @author: huanghb
 * @date: 2022/11/14 9:48
 */
@Slf4j
public abstract class ActivityDailyTaskService {
    @Autowired
    private AwardService awardService;
    @Autowired
    private ActivityDailyTaskDataService activityDailyTaskDataService;


    /**
     * 获得任务类别
     *
     * @return
     */
    protected abstract Integer getTaskType();

    /**
     * 获得任务组
     *
     * @return
     */
    protected abstract Integer getTaskGroup();

    /**
     * 获得奖励获得方式
     *
     * @return
     */
    protected abstract WayEnum getWayEnum();

    /**
     * 是否开启活动
     *
     * @return
     */
    protected abstract boolean isOpenActivity(long uid);

    /**
     * 获取循环数据的循环标识
     *
     * @return
     */
    private String getDataLoop() {
        return "" + getTaskType() + SPLIT + DateUtil.toDateInt(DateUtil.now());
    }

    /**
     * 获得所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserActivityDailyTask> getDailyTasks(long uid) {
        List<CfgTaskEntity> cfgTaskEntities = TaskTool.getTaskConfig(getTaskGroup()).getTasks();
        List<Long> taskIds = cfgTaskEntities.stream().mapToLong(CfgTaskEntity::getId).boxed().collect(Collectors.toList());
        List<UserActivityDailyTask> tasks = activityDailyTaskDataService.getDatas(uid, taskIds, getDataLoop());
        return tasks;
    }

    /**
     * 添加所有每日任务信息
     *
     * @param tasks
     * @param uid
     */
    public void addDailyTasks(List<UserActivityDailyTask> tasks, long uid) {
        activityDailyTaskDataService.addDatas(uid, tasks);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        //获得任务信息
        List<UserActivityDailyTask> tasks = getCurUserDailyTasks(uid);
        //排序
        tasks.sort(Comparator.comparing(UserActivityDailyTask::getTaskId));
        //生成任务返回信息
        List<RDTaskItem> items = tasks.stream().map(tmp -> {
            return getRdTaskItem(tmp);
        }).collect(Collectors.toList());
        rd.setItems(items);
        return rd;
    }

    /**
     * 生成返回任务信息
     *
     * @param tmp
     * @return
     */
    private RDTaskItem getRdTaskItem(UserActivityDailyTask tmp) {
        return RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.fromValue(getTaskGroup()), tmp.getTaskId()));
    }

    /**
     * 获得所有用户每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserActivityDailyTask> getCurUserDailyTasks(long uid) {
        List<UserActivityDailyTask> tasks = getDailyTasks(uid);
        //任务已存在
        if (ListUtil.isNotEmpty(tasks)) {
            return tasks;
        }
        tasks = generateTasks(uid);
        return tasks;
    }

    /**
     * 获取某个任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public UserActivityDailyTask getCurUserDailyTask(long uid, int taskId) {
        Optional<UserActivityDailyTask> optional = activityDailyTaskDataService.getData(uid, Long.valueOf(taskId), getDataLoop());
        //任务已存在
        if (optional.isPresent()) {
            return optional.get();
        }
        List<UserActivityDailyTask> dailyTasks = generateTasks(uid);
        return dailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().get();
    }

    /**
     * 生成任务
     *
     * @param uid
     * @return
     */
    private List<UserActivityDailyTask> generateTasks(long uid) {
        // 生成任务
        CfgTaskConfig config = TaskTool.getTaskConfig(getTaskGroup());
        List<CfgTaskEntity> cfgTasks = config.getTasks();
        // 转对象
        List<UserActivityDailyTask> toAddTasks = cfgTasks.stream().map(t -> UserActivityDailyTask.fromTask(uid, t)).collect(Collectors.toList());
        addDailyTasks(toAddTasks, uid);
        return toAddTasks;
    }

    /**
     * 领取任务奖励
     *
     * @param uid
     * @param taskId
     * @return
     */
    public RDCommon gainTaskAward(long uid, int taskId) {
        RDCommon rd = new RDCommon();
        List<UserActivityDailyTask> tasks = getDailyTasks(uid);
        UserActivityDailyTask task = tasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
        // 任务不存在
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        // 奖励已领取
        if (TaskStatusEnum.AWARDED.getValue() == task.getStatus()) {
            throw new ExceptionForClientTip("task.already.award");
        }

        // 未达成
        if (TaskStatusEnum.DOING.getValue() == task.getStatus()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        //获得奖励
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.fromValue(getTaskGroup()), taskId);
        List<Award> finalAwards = new ArrayList<>();
        task.HandleProgressAndAward(finalAwards, awards);
        // 发送奖励
        awardService.sendNeedMergedAwards(uid, finalAwards, getWayEnum(), "", rd);
        activityDailyTaskDataService.updateData(task);

        return rd;
    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param taskId
     * @param value
     */
    public void addProgress(long uid, int taskId, int value) {
        if (!isOpenActivity(uid)) {
            return;
        }
        //获取任务
        UserActivityDailyTask task = getCurUserDailyTask(uid, taskId);
        if (null == task) {
            return;
        }
        boolean isTodaytask = isTodaytask(task);
        if (!isTodaytask) {
            log.info("不是今日任务,记录任务id{}和进度信息{}", taskId, value);
            return;
        }
        if (task.getStatus() == TaskStatusEnum.AWARDED.getValue()) {
            return;
        }
        if (!task.ifHasRemainingTimes()) {
            return;
        }
        task.addProgress(value);
        activityDailyTaskDataService.updateData(task);
        redNotice(uid, taskId, task);
    }


    /**
     * 增加多个任务进度
     *
     * @param uid
     * @param taskIds
     * @param value
     */
    public void addProgress(long uid, List<Integer> taskIds, int value) {
        for (Integer taskId : taskIds) {
            addProgress(uid, taskId, value);
        }
    }

    /**
     * 是否今天任务
     *
     * @param task
     * @return
     */
    private boolean isTodaytask(UserActivityDailyTask task) {
        Date taskGenerateTime = DateUtil.fromDateLong(task.getGenerateTime());
        return DateUtil.isToday(taskGenerateTime);
    }

    /**
     * 发送红点
     *
     * @param uid
     * @param taskId
     * @param task
     */
    private void redNotice(long uid, int taskId, UserActivityDailyTask task) {
        if (task.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.TASK, getTaskType(), taskId);
        }
    }
}