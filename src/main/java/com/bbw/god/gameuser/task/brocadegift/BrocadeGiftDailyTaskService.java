package com.bbw.god.gameuser.task.brocadegift;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.gameuser.task.brocadegift.BrocadeGiftDailyTaskDataService.CACHE_TIME;

/**
 * 锦礼每日任务service
 *
 * @author: huanghb
 * @date: 2022/2/17 14:47
 */
@Service
public class BrocadeGiftDailyTaskService {
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private BrocadeGiftDailyTaskDataService brocadeGiftDailyTaskDataService;

    /**
     * 获得锦礼所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserBrocadeGiftDailyTask> getBrocadeGiftDailyTasks(long uid) {
        List<UserBrocadeGiftDailyTask> tasks = brocadeGiftDailyTaskDataService.getBrocadeGiftDailyTasksFromCache(uid);
        CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.BROCADE_GIFT_DAILY_TASK);
        List<Integer> taskIds = config.getTasks().stream().map(CfgTaskEntity::getId).collect(Collectors.toList());
        tasks.removeIf(tmp -> !taskIds.contains(tmp.getTaskId()));
        if (ListUtil.isEmpty(tasks)) {
            return new ArrayList<>();
        }
        //有效任务时间
        Date validTaskTime = DateUtil.addDays(DateUtil.now(), -CACHE_TIME);
        return tasks.stream().filter(tmp -> DateUtil.fromDateLong(tmp.getGenerateTime()).after(validTaskTime)).collect(Collectors.toList());
    }

    /**
     * 添加锦礼所有每日任务信息
     *
     * @param tasks
     * @param uid
     */
    public void addBrocadeGiftDailyTasks(List<UserBrocadeGiftDailyTask> tasks, long uid) {
        brocadeGiftDailyTaskDataService.updateBrocadeGiftDailyTasksToCache(uid, tasks);
    }

    /**
     * 获得锦礼所有每日任务信息
     *
     * @param uid
     * @param days
     * @return
     */
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        //获得任务信息
        List<UserBrocadeGiftDailyTask> tasks = getCurUseraBrocadeGiftDailyTasks(uid);
        //排序
        tasks.sort(Comparator.comparing(UserBrocadeGiftDailyTask::getTaskId));
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
    private RDTaskItem getRdTaskItem(UserBrocadeGiftDailyTask tmp) {
        return RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.BROCADE_GIFT_DAILY_TASK, tmp.getTaskId()));
    }

    /**
     * 获得当前锦礼所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserBrocadeGiftDailyTask> getCurUseraBrocadeGiftDailyTasks(long uid) {
        List<UserBrocadeGiftDailyTask> tasks = getBrocadeGiftDailyTasks(uid);
        CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.BROCADE_GIFT_DAILY_TASK);
        List<CfgTaskEntity> cfgTasks = config.getTasks();
        List<Integer> cfgTaskIds = cfgTasks.stream().map(CfgTaskEntity::getId).collect(Collectors.toList());
        List<CfgTaskEntity> cfgTasksClone = CloneUtil.cloneList(cfgTasks);
        //任务已存在且无缺失
        boolean isAllTask = ListUtil.isNotEmpty(tasks) && tasks.size() >= cfgTaskIds.size();
        if (isAllTask) {
            return tasks;
        }
        List<Integer> userTaskIds = tasks.stream().map(UserBrocadeGiftDailyTask::getTaskId).collect(Collectors.toList());
        //去除不需要生成的任务
        if (ListUtil.isNotEmpty(userTaskIds)) {
            cfgTasksClone.removeIf(tmp -> userTaskIds.contains(tmp.getId()));
        }
        // 生成任务
        List<UserBrocadeGiftDailyTask> newTasks = (List<UserBrocadeGiftDailyTask>) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
            List<UserBrocadeGiftDailyTask> toAddTasks = new ArrayList<>();
            // 转对象
            List<UserBrocadeGiftDailyTask> annualGiftDailyTasks = cfgTasksClone.stream().map(t -> UserBrocadeGiftDailyTask.fromTask(t)).collect(Collectors.toList());
            // 保存数据
            toAddTasks.addAll(annualGiftDailyTasks);
            addBrocadeGiftDailyTasks(toAddTasks, uid);
            if (ListUtil.isNotEmpty(userTaskIds)) {
                toAddTasks.addAll(tasks);
            }
            return toAddTasks;
        });
        return newTasks;
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
        List<UserBrocadeGiftDailyTask> userBrocadeGiftDailyTasks = getBrocadeGiftDailyTasks(uid);
        UserBrocadeGiftDailyTask task = userBrocadeGiftDailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
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
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.BROCADE_GIFT_DAILY_TASK, taskId);
        // 发送奖励
        awardService.fetchAward(uid, awards, WayEnum.BROCADE_GIFT_DAILY_TASK, "通过锦礼每日任务获得", rd);
        // 修改状态
        task.setStatus(TaskStatusEnum.AWARDED.getValue());
        brocadeGiftDailyTaskDataService.updateBrocadeGiftDailyTaskToCache(uid, task);

        return rd;
    }
}