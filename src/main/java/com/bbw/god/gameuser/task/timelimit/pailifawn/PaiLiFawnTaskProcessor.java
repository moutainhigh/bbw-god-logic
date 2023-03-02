package com.bbw.god.gameuser.task.timelimit.pailifawn;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.businessgang.UserSpecialtyShippingTaskService;
import com.bbw.god.gameuser.task.businessgang.UserWeeklyTaskService;
import com.bbw.god.gameuser.task.timelimit.CfgTimeLimitTaskRules;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 派礼小鹿任务实现类
 *
 * @author: huanghb
 * @date: 2022/12/9 17:39
 */
@Service
public class PaiLiFawnTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    UserWeeklyTaskService userWeeklyTaskService;
    @Autowired
    private PaiLiFawnLimitTaskService paiLiFawnLimitTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;

    public PaiLiFawnTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.PAI_LI_FAWN_51);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        //获得所有未过期任务
        List<UserTimeLimitTask> dispatchTask = getDispatchTask(uid);
        dispatchTask = dispatchTask.stream().filter(tmp -> tmp.getStatus() != TaskStatusEnum.FAIL.getValue()).collect(Collectors.toList());
        List<RDTaskItem> rdTaskItem = getRdTaskItems(dispatchTask);
        RDTaskList rd = new RDTaskList();
        rd.addTasks(rdTaskItem);
        return rd;
    }

    /**
     * 获取任务奖励
     *
     * @param uid
     * @param id
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 获取派遣任务
     *
     * @param uid
     * @return
     */
    private List<UserTimeLimitTask> getDispatchTask(long uid) {
        //获取派遣任务a
        TaskGroupEnum taskGroupEnum = TaskGroupEnum.PAI_LI_FAWN_51;
        List<UserTimeLimitTask> tasks = userTimeLimitTaskService.getTasks(uid, taskGroupEnum);
        //获取任务规则
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroupEnum);
        //获取最大等待任务数量
        Integer maxWaitingTaskNum = rules.getMaxWaitingTaskNum();
        //不存在则生成
        if (ListUtil.isEmpty(tasks)) {
            //初始化任务
            List<UserTimeLimitTask> userTimeLimitTasks = paiLiFawnLimitTaskService.makeUserTaskInstance(uid, maxWaitingTaskNum);
            return userTimeLimitTasks;
        }
        //获得等待任务
        List<UserTimeLimitTask> waitTask = tasks.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.WAITING.getValue()).collect(Collectors.toList());
        //如果等待任务不到上限则补足
        if (waitTask.size() < maxWaitingTaskNum) {
            List<UserTimeLimitTask> userTimeLimitTasks = paiLiFawnLimitTaskService.makeUserTaskInstance(uid, maxWaitingTaskNum - waitTask.size());
            tasks.addAll(userTimeLimitTasks);
        }
        //更新
        gameUserService.updateItems(tasks);
        return tasks;
    }

    /**
     * 封装返回格式
     *
     * @param tasks
     * @return
     */
    private List<RDTaskItem> getRdTaskItems(List<UserTimeLimitTask> tasks) {
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        for (UserTimeLimitTask task : tasks) {
            TaskGroupEnum taskGroupEnum = TaskGroupEnum.fromValue(task.getGroup());
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroupEnum, task.getBaseId());
            RDTaskItem rdTaskItem = RDTaskItem.getInstance(task, taskEntity, taskGroupEnum);
            rdTaskItems.add(rdTaskItem);
        }
        return rdTaskItems;
    }

    /**
     * 领奖励
     *
     * @param userTimeLimitTask
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask userTimeLimitTask) {
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(userTimeLimitTask);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(userTimeLimitTask, taskStatus);
        gameUserService.updateItem(userTimeLimitTask);
        return rd;
    }

    /**
     * 有多少个可领取的任务
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getAbleAwardedTask(long uid) {
        return getDispatchTask(uid);
    }
}
