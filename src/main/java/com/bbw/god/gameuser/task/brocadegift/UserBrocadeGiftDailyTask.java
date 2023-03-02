package com.bbw.god.gameuser.task.brocadegift;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家锦礼每日任务
 *
 * @author: huanghb
 * @date: 2022/2/8 10:09
 */
@Data
public class UserBrocadeGiftDailyTask implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 任务id */
    private int taskId;
    /** 最新一个进度完成时间 */
    private long accomplishTime = 0;
    /** 任务生成时间yyyyMMddHHmmss */
    private Long generateTime = DateUtil.toDateTimeLong();
    /** 任务类别 */
    private Integer taskType = TaskTypeEnum.ANNUAL_GIFT_DAILY_TASK.getValue();
    /** 开启天数 */
    private Integer days;
    /** 完成任务需要的进度 */
    private Integer needProgress;
    /** 当前进度 */
    private long progress = 0;
    /** 状态 */
    private Integer status = TaskStatusEnum.DOING.getValue();
    /** 任务名称 */
    private String name;

    public static UserBrocadeGiftDailyTask fromTask(CfgTaskEntity task) {
        UserBrocadeGiftDailyTask userBrocadeGiftDailyTask = new UserBrocadeGiftDailyTask();
        userBrocadeGiftDailyTask.setTaskId(task.getId());
        userBrocadeGiftDailyTask.setNeedProgress(task.getValue());
        userBrocadeGiftDailyTask.setName(task.getName());
        userBrocadeGiftDailyTask.setDays(task.getDays());
        return userBrocadeGiftDailyTask;
    }

    /**
     * 增加进度
     *
     * @param addProgress
     */
    public void addProgress(long addProgress) {
        this.progress += addProgress;
        if (this.progress >= this.needProgress) {
            this.progress = this.needProgress;
            this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
        this.accomplishTime = DateUtil.toDateTimeLong();
    }

    /**
     * 清理进度
     */
    public void cleanProgress() {
        this.progress = 0;
        this.status = TaskStatusEnum.DOING.getValue();
        this.generateTime = DateUtil.toDateTimeLong();
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
