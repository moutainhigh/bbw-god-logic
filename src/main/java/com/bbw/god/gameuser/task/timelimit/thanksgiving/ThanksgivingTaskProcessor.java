package com.bbw.god.gameuser.task.timelimit.thanksgiving;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.holiday.processor.HolidayCunZYiYunProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.UserDispatchTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 感恩节任务
 *
 * @author fzj
 * @date 2021/11/19 8:55
 */
@Service
public class ThanksgivingTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserThanksgivingTaskService userThanksgivingTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private HolidayCunZYiYunProcessor holidayCunZYiYun;


    public ThanksgivingTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.THANKS_GIVING_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        return new RDTaskList();
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 获取任务信息
     *
     * @param uid
     * @return
     */
    public RDSuccess getTask(long uid) {
        if (!isJoined(uid)) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.THANKS_GIVING_TASK, 120001);
            UserTimeLimitTask userTimeLimitTask = userThanksgivingTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setJoinStatus(uid);
        }
        List<UserTimeLimitTask> uts = userThanksgivingTaskService.getAllTasks(uid);
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.THANKS_GIVING_TASK, ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.THANKS_GIVING_TASK).size();
        boolean executable = holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum);
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, executable);
        }
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.THANKS_GIVING_TASK);
        String[] titleFormats = {String.valueOf(ut.getValue())};
        rdTask.setTitleFormats(titleFormats);
        //判断当前任务是否可以执行
        if (!executable) {
            rdTask.setIsExecutable(1);
        }
        RDTaskInfo rd = new RDTaskInfo();
        rd.setTaskItem(rdTask);
        return rd;
    }

    /**
     * 领奖励
     *
     * @param ut
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask ut) {
        long uid = ut.getGameUserId();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.THANKS_GIVING_TASK).size();
        //检查是否可以生成下一个任务
        if (!holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum)) {
            return new RDCommon();
        }
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
        // 生成下一个任务
        int taskSeq = taskEntity.getSeq() + 1;
        CfgTaskEntity nextTask = TaskTool.getTask(TaskGroupEnum.fromValue(ut.getGroup()), taskEntity.getSeqGroup(), taskSeq);
        if (null != nextTask) {
            UserTimeLimitTask nextUt = userThanksgivingTaskService.makeUserTaskInstance(uid, nextTask);
            gameUserService.addItem(uid, nextUt);
        }
        return rd;
    }


    /**
     * 判断是否参加感恩节村庄任务
     *
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinThanksgivingTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinThanksgivingTask", Integer.class);
        return null != hasJoinThanksgivingTask && hasJoinThanksgivingTask == 1;
    }

    /**
     * 保存感恩节村庄任务村庄状态
     *
     * @param uid
     */
    private void setJoinStatus(long uid) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinThanksgivingTask", 1, DateUtil.SECOND_ONE_DAY * 10);
    }
}
