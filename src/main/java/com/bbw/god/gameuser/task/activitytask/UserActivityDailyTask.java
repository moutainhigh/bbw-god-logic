package com.bbw.god.gameuser.task.activitytask;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTool;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家活动每日任务
 *
 * @author: huanghb
 * @date: 2022/10/14 9:20
 */
@Data
public class UserActivityDailyTask extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 1L;
    private long uid;
    /** 任务id */
    private int taskId;
    /** 最新一个进度完成时间 */
    private long accomplishTime = 0;
    /** 任务生成时间yyyyMMddHHmmss */
    private Long generateTime = DateUtil.toDateTimeLong();
    /** 任务类别 */
    private Integer taskType;
    /** 完成任务需要的进度 */
    private Integer needProgress;
    /** 当前进度 */
    private long progress = 0;
    /** 状态 */
    private Integer status = TaskStatusEnum.DOING.getValue();
    /** 任务名称 */
    private String name;
    /** 完成次数 */
    private Integer timesOfAccomplish = 0;

    public static UserActivityDailyTask fromTask(long uid, CfgTaskEntity task) {
        UserActivityDailyTask userActivityDailyTask = new UserActivityDailyTask();
        userActivityDailyTask.setUid(uid);
        userActivityDailyTask.setId(Long.valueOf(task.getId()));
        userActivityDailyTask.setTaskId(task.getId());
        userActivityDailyTask.setTaskType(task.getType());
        userActivityDailyTask.setNeedProgress(task.getValue());
        userActivityDailyTask.setName(task.getName());
        return userActivityDailyTask;
    }


    /**
     * 获得总完成次数
     *
     * @return
     */
    public Integer gainTotalTimesOfAccomplish() {
        int unGainAwardTimes = progress > 0 ? (int) (progress / needProgress) : 0;
        return unGainAwardTimes + timesOfAccomplish;
    }

    /**
     * 获得剩余次数
     *
     * @return
     */
    public Integer gainRemainingTimes() {
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(taskId);
        int remainingTimes = cfgTaskEntity.getTimesLimit() - gainTotalTimesOfAccomplish();
        return remainingTimes > 0 ? remainingTimes : 0;
    }

    /**
     * 是否有剩余次数
     *
     * @return
     */
    public boolean ifHasRemainingTimes() {
        return gainRemainingTimes() > 0;
    }

    /**
     * 增加进度
     *
     * @param addProgress
     */
    public void addProgress(long addProgress) {
        this.progress += addProgress;
        if (this.progress >= this.needProgress) {
            this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
        this.accomplishTime = DateUtil.toDateTimeLong();
    }

    /**
     * 处理进度和奖励
     */
    public void HandleProgressAndAward(List<Award> finalAwards, List<Award> awards) {
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(this.taskId);
        while (this.progress >= this.needProgress && this.timesOfAccomplish < cfgTaskEntity.getTimesLimit()) {
            this.timesOfAccomplish++;
            this.progress -= cfgTaskEntity.getValue();
            finalAwards.addAll(awards);
        }
        this.generateTime = DateUtil.toDateTimeLong();
        if (ifHasRemainingTimes()) {
            this.status = TaskStatusEnum.DOING.getValue();
            return;
        }
        this.status = TaskStatusEnum.AWARDED.getValue();
    }

    /**
     * 任务是否达成
     *
     * @return
     */
    public boolean ifAccomplished() {
        return status >= TaskStatusEnum.ACCOMPLISHED.getValue();
    }

}
