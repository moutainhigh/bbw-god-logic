package com.bbw.god.gameuser.task.timelimit.wansj;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
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
import java.util.Optional;

/**
 * 万圣节任务
 *
 * @author: suhq
 * @date: 2021/10/20 5:27 下午
 */
@Service
public class WanSJTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private UserWanSJTaskService userWanSJTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;

    public WanSJTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.WAN_SHENG_JIE_TASK);
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
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.WAN_SHENG_JIE_TASK, 110001);
            UserTimeLimitTask userTimeLimitTask = userWanSJTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setJoinStatus(uid);
        }
        List<UserTimeLimitTask> uts = userWanSJTaskService.getAllTasks(uid);
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.WAN_SHENG_JIE_TASK, ut.getBaseId());
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, true);
        }
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.WAN_SHENG_JIE_TASK);
        String[] titleFormats = {String.valueOf(ut.getValue())};
        rdTask.setTitleFormats(titleFormats);
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
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        // 史诗自动生成下一轮
        CfgTaskEntity nextTask = TaskTool.getTask(TaskGroupEnum.fromValue(ut.getGroup()), taskEntity.getSeqGroup(), taskEntity.getSeq() + 1);
        if (null != nextTask) {
            UserTimeLimitTask nextUt = userWanSJTaskService.makeUserTaskInstance(uid, nextTask);
            gameUserService.addItem(uid, nextUt);
        }
        return rd;
    }

    /**
     * 判断是否参加万圣节村庄任务
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinWanSJTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinWanSJTask", Integer.class);
        return null != hasJoinWanSJTask && hasJoinWanSJTask == 1;
    }

    /**
     * 保存万圣节村庄任务村庄状态
     * @param uid
     */
    private void setJoinStatus(long uid) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinWanSJTask", 1, DateUtil.SECOND_ONE_DAY * 10);
    }


}
