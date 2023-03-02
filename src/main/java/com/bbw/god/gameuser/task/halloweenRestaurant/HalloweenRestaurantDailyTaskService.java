package com.bbw.god.gameuser.task.halloweenRestaurant;

import com.bbw.common.CloneUtil;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 万圣餐厅每日任务service
 *
 * @author: huanghb
 * @date: 2022/10/14 9:18
 */
@Service
public class HalloweenRestaurantDailyTaskService {
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private HalloweenRestaurantDailyTaskDataService halloweenRestaurantDailyTaskDataService;
    /** 每日合成任务id */
    public static final List<Integer> DAILY_COMPOUND_TASK_IDS = Arrays.asList(230001, 230002, 230003, 230004);
    /** 每日元宝购买任务 */
    public static final List<Integer> DAILY_GOLD_TASK_IDS = Arrays.asList(230005, 230006, 230007, 230008);
    /** 每日使用法宝任务（仅限地图法宝和战斗法宝） */
    public static final List<Integer> DAILY_USE_TREASURE_TASK_IDS = Arrays.asList(230011, 230012);
    /** 使用法宝任务完成次数上限 */
    public static final int USE_TREASURE_TIMES_LIMIT = 100;

    /**
     * 获得所有每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserHalloweenRestaurantDailyTask> getDailyTasks(long uid) {
        return halloweenRestaurantDailyTaskDataService.getDailyTasksFromCache(uid);
    }

    /**
     * 添加所有每日任务信息
     *
     * @param tasks
     * @param uid
     */
    public void addDailyTasks(List<UserHalloweenRestaurantDailyTask> tasks, long uid) {
        halloweenRestaurantDailyTaskDataService.updateDailyTasksToCache(uid, tasks);
    }

    /**
     * 获得所有每日任务信息
     *
     * @param uid
     * @param days
     * @return
     */
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        //获得任务信息
        List<UserHalloweenRestaurantDailyTask> tasks = getCurUserDailyTasks(uid);
        //排序
        tasks.sort(Comparator.comparing(UserHalloweenRestaurantDailyTask::getTaskId));
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
    private RDTaskItem getRdTaskItem(UserHalloweenRestaurantDailyTask tmp) {
        return RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK, tmp.getTaskId()));
    }

    /**
     * 获得所有用户每日任务信息
     *
     * @param uid
     * @return
     */
    public List<UserHalloweenRestaurantDailyTask> getCurUserDailyTasks(long uid) {
        List<UserHalloweenRestaurantDailyTask> tasks = getDailyTasks(uid);
        //任务已存在
        if (ListUtil.isNotEmpty(tasks)) {
            for (UserHalloweenRestaurantDailyTask task : tasks) {
                //修复负进度任务
                CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(task.getTaskId());
                int taskProgress = (int) (task.getProgress() - task.getNeedProgress() + cfgTaskEntity.getValue());
                if (0 > taskProgress) {
                    task.cleanProgress();
                    halloweenRestaurantDailyTaskDataService.updateDailyTaskToCache(uid, task);
                }
            }
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
    public UserHalloweenRestaurantDailyTask getCurUserDailyTask(long uid, int taskId) {
        UserHalloweenRestaurantDailyTask dailyTask = halloweenRestaurantDailyTaskDataService.getDailyTaskFromCache(uid, taskId);
        //任务已存在
        if (null != dailyTask) {
            return dailyTask;
        }
        List<UserHalloweenRestaurantDailyTask> dailyTasks = generateTasks(uid);
        dailyTask = dailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().get();
        return dailyTask;
    }

    /**
     * 生成任务
     *
     * @param uid
     * @return
     */
    private List<UserHalloweenRestaurantDailyTask> generateTasks(long uid) {
        // 生成任务
        CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK);
        List<CfgTaskEntity> cfgTasks = config.getTasks();
        // 转对象
        List<UserHalloweenRestaurantDailyTask> toAddTasks = cfgTasks.stream().map(t -> UserHalloweenRestaurantDailyTask.fromTask(t)).collect(Collectors.toList());
//        List<UserHalloweenRestaurantDailyTask> dailyTasks = getDailyTasks(uid);
//        if (ListUtil.isNotEmpty(dailyTasks)){
//            return dailyTasks;
//        }
        addDailyTasks(toAddTasks, uid);
        return toAddTasks;
//        return toAddTasks;
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
        List<UserHalloweenRestaurantDailyTask> tasks = getDailyTasks(uid);
        UserHalloweenRestaurantDailyTask task = tasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
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
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK, taskId);
        List<Award> finalAwards = new ArrayList<>();
        //是否每日任务
        List<Integer> dailyTask = new ArrayList<>();
        dailyTask.addAll(DAILY_COMPOUND_TASK_IDS);
        dailyTask.addAll(DAILY_GOLD_TASK_IDS);
        boolean isDailyOneTimesTask = dailyTask.contains(taskId);
        // 开启下一个-任务进度
        if (!isDailyOneTimesTask) {
            task.openNextProgressAndHandleAward(finalAwards, CloneUtil.cloneList(awards));
        }
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(taskId);
        //
        task.setStatus(TaskStatusEnum.DOING.getValue());
        //是否每日使用法宝任务完成次数达到上限
        boolean isUseTreasureTaskTimesLimit = task.getNeedProgress() / cfgTaskEntity.getValue() > USE_TREASURE_TIMES_LIMIT;
        if (isUseTreasureTaskTimesLimit && DAILY_USE_TREASURE_TASK_IDS.contains(taskId)) {
            task.setStatus(TaskStatusEnum.AWARDED.getValue());
        }
        // 修改状态-每日任务
        if (isDailyOneTimesTask) {
            finalAwards.addAll(CloneUtil.cloneList(awards));
            task.setStatus(TaskStatusEnum.AWARDED.getValue());
        }
        // 发送奖励
        awardService.sendNeedMergedAwards(uid, finalAwards, WayEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK, "通过" + WayEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK.getName() + "获得", rd);

        halloweenRestaurantDailyTaskDataService.updateDailyTaskToCache(uid, task);

        return rd;
    }
}