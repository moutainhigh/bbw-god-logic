package com.bbw.god.gameuser.task.businessgang.yingjie;

import com.bbw.common.ListUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮英杰任务service
 *
 * @author: huanghb
 * @date: 2022/7/25 15:07
 */
@Service
public class BusinessGangYingJieTaskService {
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private BusinessGangYingJieTaskDataService businessGangYingJieTaskDataService;

    /**
     * 获得所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangYingJieTask> getUserBusinessGangYingJieTasks(long uid) {
        return businessGangYingJieTaskDataService.getBusinessGangYingJieTasksFromCache(uid);
    }

    /**
     * 添加锦礼所有每日任务信息
     *
     * @param tasks
     * @param uid
     */
    public void addBusinessGangYingJieTasks(List<UserBusinessGangYingJieTask> tasks, long uid) {
        businessGangYingJieTaskDataService.updateBusinessGangYingJieTasksToCache(uid, tasks);
    }

    /**
     * 获得所有任务信息
     *
     * @param uid
     * @param days
     * @return
     */
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        //获得任务信息
        List<UserBusinessGangYingJieTask> tasks = getCurUseraUserBusinessGangYingJieTasks(uid);
        //排序
        tasks.sort(Comparator.comparing(UserBusinessGangYingJieTask::getTaskId));
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
    private RDTaskItem getRdTaskItem(UserBusinessGangYingJieTask tmp) {
        return RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.BUSINESS_GANG_YINGJIE_TASK, tmp.getTaskId()));
    }

    /**
     * 获得当前锦礼所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangYingJieTask> getCurUseraUserBusinessGangYingJieTasks(long uid) {
        List<UserBusinessGangYingJieTask> tasks = getUserBusinessGangYingJieTasks(uid);
        //任务已存在
        if (ListUtil.isNotEmpty(tasks)) {
            return tasks;
        }
        // 生成任务
        tasks = new ArrayList<>();
        CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.BUSINESS_GANG_YINGJIE_TASK);
        List<CfgTaskEntity> cfgTasks = config.getTasks();
        // 转对象
        List<UserBusinessGangYingJieTask> businessGangYingJieTasks = cfgTasks.stream().map(t -> UserBusinessGangYingJieTask.fromTask(t)).collect(Collectors.toList());
        // 保存数据
        tasks.addAll(businessGangYingJieTasks);
        addBusinessGangYingJieTasks(tasks, uid);
        return tasks;
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
        List<UserBusinessGangYingJieTask> userBrocadeGiftDailyTasks = getUserBusinessGangYingJieTasks(uid);
        UserBusinessGangYingJieTask task = userBrocadeGiftDailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
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
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.BUSINESS_GANG_YINGJIE_TASK, taskId);
        // 发送奖励
        awardService.fetchAward(uid, awards, WayEnum.BUSINESS_GANG_YINGJIE, "通过商帮英杰任务获得", rd);
        // 修改状态
        task.setStatus(TaskStatusEnum.AWARDED.getValue());
        businessGangYingJieTaskDataService.updateBusinessGangYingJieTaskToCache(uid, task);

        return rd;
    }
}