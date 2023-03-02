package com.bbw.god.gameuser.task.biggodplan;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 大仙计划service
 *
 * @author: huanghb
 * @date: 2022/2/17 14:47
 */
@Service
public class BigGodPlanTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private BigGodPlanTaskDataService bigGodPlanTaskDataService;
    @Autowired
    private BigGodPlanTaskService bigGodPlanTaskService;


    /** 最大天数 */
    private static final int MAX_MATCH_DAYS = 7;

    /**
     * 获得所有大仙计划任务信息
     *
     * @param uid
     * @return
     */
    public List<UserBigGodPlanTask> getbigGodPlanTasks(long uid) {
        return bigGodPlanTaskDataService.getBigGodPlanTasksFromCache(uid);
    }

    /**
     * 添加所有大仙计划任务
     *
     * @param tasks
     * @param uid
     */
    public void addBigGodPlanTasks(List<UserBigGodPlanTask> tasks, long uid) {
        bigGodPlanTaskDataService.updateBigGodPlanTasksToCache(uid, tasks);
    }

    /**
     * 获得大仙计划任务列表
     *
     * @param uid
     * @param days
     * @return
     */
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        //获得任务信息
        List<UserBigGodPlanTask> tasks = getCurUserBigGodPlanTasks(uid);
        //排序
        tasks.sort(Comparator.comparing(UserBigGodPlanTask::getTaskId));
        //任务开放天数
        int openDays = getOpenDays(uid);
        //任务是否开放
        if (null != days && days > openDays) {
            throw new ExceptionForClientTip("task.big.god.plan.is.lock");
        }
        //最终需要显示任务的天数
        int matchDays = null == days ? openDays : days;
        matchDays = Math.min(matchDays, MAX_MATCH_DAYS);
        int finalMatchDays = matchDays;
        //生成任务返回信息
        tasks = tasks.stream().filter(tmp -> finalMatchDays == tmp.getDays() || tmp.getDays() == 0).collect(Collectors.toList());
        List<RDTaskItem> items = tasks.stream().map(tmp -> {
            return getRdTaskItem(tmp);
        }).collect(Collectors.toList());
        rd.setItems(items);
        rd.setCurDays(matchDays);
        return rd;
    }

    /**
     * 生成返回任务信息
     *
     * @param tmp
     * @return
     */
    private RDTaskItem getRdTaskItem(UserBigGodPlanTask tmp) {
        return RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.BIG_DOG_PLAN, tmp.getTaskId()));
    }

    /**
     * 任务开放天数
     *
     * @param uid
     * @return
     */
    public int getOpenDays(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity iActivity = activityService.getActivity(sid, ActivityEnum.BIG_GOD_PLAN);
        Date beginTime = iActivity.gainBegin();
        Date now = DateUtil.now();
        return DateUtil.getDaysBetween(beginTime, now) + 1;
    }

    /**
     * 获得当前大仙计划所有任务
     *
     * @param uid
     * @return
     */
    public List<UserBigGodPlanTask> getCurUserBigGodPlanTasks(long uid) {
        List<UserBigGodPlanTask> tasks = getbigGodPlanTasks(uid);
        // 任务不存在
        if (ListUtil.isEmpty(tasks)) {
            // 生成任务
            tasks = (List<UserBigGodPlanTask>) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                List<UserBigGodPlanTask> toAddTasks = new ArrayList<>();
                CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.BIG_DOG_PLAN);
                List<CfgTaskEntity> cfgTasks = config.getTasks();
                List<CfgTaskConfig.CfgBox> boxes = config.getBoxs();
                // 转对象
                List<UserBigGodPlanTask> bigGodPlanTasks = cfgTasks.stream().map(t -> UserBigGodPlanTask.fromTask(t)).collect(Collectors.toList());
                List<UserBigGodPlanTask> bigGodPlanBoxes = boxes.stream().map(t -> UserBigGodPlanTask.fromTask(t)).collect(Collectors.toList());
                // 保存数据
                toAddTasks.addAll(bigGodPlanTasks);
                toAddTasks.addAll(bigGodPlanBoxes);
                addBigGodPlanTasks(toAddTasks, uid);
                return null;
            });
        }
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
        List<UserBigGodPlanTask> userBigGodPlanTasks = getbigGodPlanTasks(uid);
        UserBigGodPlanTask task = userBigGodPlanTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
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
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.BIG_DOG_PLAN, taskId);
        boolean isBoxTask = TaskTool.isBoxTask(taskId);
        WayEnum way = isBoxTask ? WayEnum.OPEN_GOD_TRAINING_BOX : WayEnum.OPEN_GOD_TRAINING_TASK;
        // 发送奖励
        awardService.fetchAward(uid, awards, way, "通过大仙计划任务获得", rd);
        // 修改状态
        task.setStatus(TaskStatusEnum.AWARDED.getValue());
        bigGodPlanTaskDataService.updateBigGodPlanTaskToCache(uid, task);

        return rd;
    }
}