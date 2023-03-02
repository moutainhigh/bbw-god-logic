package com.bbw.god.gameuser.task.businessgang;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.rd.RDBusinessGangTaskInfo;
import com.bbw.god.gameuser.businessgang.rd.RDSpecialtyShippingInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.businessGang.UserBusinessGangLimitTaskService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 商帮任务实现类
 *
 * @author fzj
 * @date 2022/1/18 17:08
 */
@Service
public class BusinessGangTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    UserWeeklyTaskService userWeeklyTaskService;
    @Autowired
    UserBusinessGangLimitTaskService userBusinessGangLimitTaskService;
    @Autowired
    BusinessGangService businessGangService;
    @Autowired
    UserBusinessGangService userBusinessGangService;

    public BusinessGangTaskProcessor() {
        this.taskTypes = Arrays.asList(
                TaskTypeEnum.BUSINESS_GANG_WEEKLY_TASK,
                TaskTypeEnum.BUSINESS_GANG_DISPATCH_TASK,
                TaskTypeEnum.BUSINESS_GANG_SHIPPING_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        boolean joinBusinessGang = businessGangService.isJoinBusinessGang(uid);
        if (!joinBusinessGang) {
            throw new ExceptionForClientTip("businessGang.need.join");
        }
        RDBusinessGangTaskInfo rd = new RDBusinessGangTaskInfo();
        UserBusinessGangTaskInfo userBusinessGangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        businessGangService.privilegeHand(uid, userBusinessGangTask);
        rd.setAwardableNum(userBusinessGangTask.getAwardableNum());
        rd.setFreeRefreshTaskNum(userBusinessGangTask.getFreeRefreshTaskNum());
        rd.addTasks(getSpecialtyShippingTask(uid));
        rd.addTasks(getWeeklyTask(uid));
        rd.addTasks(getDispatchTask(uid));
        return rd;
    }

    /**
     * 获得特产运送任务
     *
     * @param uid
     * @return
     */
    public List<RDTaskItem> getSpecialtyShippingTask(long uid) {
        List<UserBusinessGangSpecialtyShippingTask> allTasks = userSpecialtyShippingTaskService.getAllTasks(uid);
        List<RDTaskItem> rd = new ArrayList<>();
        for (UserBusinessGangSpecialtyShippingTask task : allTasks) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, task.getBaseId());
            RDSpecialtyShippingInfo instance = RDSpecialtyShippingInfo.getInstance(task, taskEntity);
            rd.add(instance);
        }
        return rd;
    }

    /**
     * 获取每周任务
     *
     * @param uid
     * @return
     */
    private List<RDTaskItem> getWeeklyTask(long uid) {
        //生成周常任务
        businessGangService.generateWeeklyTask(uid);
        //获取所有周常任务
        List<UserBusinessGangWeeklyTask> allTasks = userWeeklyTaskService.getAllTasks(uid);
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        if (allTasks.isEmpty()){
            return rdTaskItems;
        }
        for (UserBusinessGangWeeklyTask weeklyTask : allTasks) {
            Integer baseId = weeklyTask.getBaseId();
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK, baseId);
            RDTaskItem rdTaskItem = RDTaskItem.getInstance(weeklyTask, taskEntity);
            long remainTime = userWeeklyTaskService.getRemainTime(weeklyTask);
            rdTaskItem.setRemainTime(remainTime);
            rdTaskItems.add(rdTaskItem);
        }
        return rdTaskItems;
    }

    /**
     * 获取派遣任务
     *
     * @param uid
     * @return
     */
    private List<RDTaskItem> getDispatchTask(long uid) {
        List<UserTimeLimitTask> tasks = userBusinessGangLimitTaskService.getTasks(uid);
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        for (UserTimeLimitTask task : tasks) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK, task.getBaseId());
            RDTaskItem rdTaskItem = RDTaskItem.getInstance(task, taskEntity, TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK);
            rdTaskItems.add(rdTaskItem);
        }
        return rdTaskItems;
    }


    @Override
    public RDCommon gainTaskAward(long uid, int taskId, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 发放任务奖励
     *
     * @param uid
     * @param type
     * @param dataId
     * @return
     */
    public RDCommon gainTaskAward(long uid, int type, long dataId) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(type);
        //玩家所在商帮
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        Integer businessGang = userBusinessGang.getCurrentBusinessGang();
        UserBusinessGangTaskInfo gangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        RDCommon rd = new RDCommon();
        switch (taskType) {
            case BUSINESS_GANG_SHIPPING_TASK:
                userSpecialtyShippingTaskService.sendAwards(uid, dataId, businessGang, gangTask, rd);
                break;
            case BUSINESS_GANG_WEEKLY_TASK:
                userWeeklyTaskService.sendAwards(uid, dataId, businessGang, rd);
                return rd;
            case BUSINESS_GANG_DISPATCH_TASK:
                userBusinessGangLimitTaskService.sendAwards(uid, dataId, businessGang, gangTask, rd);
                break;
            default:
                throw new ExceptionForClientTip("task.not.exist");
        }
        //扣除可领奖次数
        if (TaskTypeEnum.BUSINESS_GANG_WEEKLY_TASK != taskType) {
            gangTask.delAwardableNum(1);
        }
        gameUserService.updateItem(gangTask);
        return rd;
    }
}
