package com.bbw.god.gameuser.task.biggodplan;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.task.CfgTaskConfig;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家大仙计划
 *
 * @author: huanghb
 * @date: 2022/2/8 10:09
 */
@Data
public class UserBigGodPlanTask implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 任务id */
    private int taskId;
    /** 完成事件时间 */
    private long accomplishTime = 0;
    /** 任务生成时间yyyyMMddHHmmss */
    private Long generateTime = DateUtil.toDateTimeLong();
    /** 任务类别 */
    private Integer taskType = TaskTypeEnum.BIG_GOD_PLAN.getValue();
    /** 开启天数 */
    private Integer days;
    /** 完成任务需要的目标值 */
    private Integer needValue;
    /** 当前进度 */
    private long value = 0;
    /** 状态 */
    private Integer status = TaskStatusEnum.DOING.getValue();
    /** 任务名称 */
    private String name;

    public static UserBigGodPlanTask fromTask(CfgTaskEntity task) {
        UserBigGodPlanTask userBigGodPlanTask = new UserBigGodPlanTask();
        userBigGodPlanTask.setTaskId(task.getId());
        userBigGodPlanTask.setNeedValue(task.getValue());
        userBigGodPlanTask.setName(task.getName());
        userBigGodPlanTask.setDays(task.getDays());
        return userBigGodPlanTask;
    }

    public static UserBigGodPlanTask fromTask(CfgTaskConfig.CfgBox task) {
        UserBigGodPlanTask userGodTrainingTask = new UserBigGodPlanTask();
        userGodTrainingTask.setTaskId(task.getId());
        userGodTrainingTask.setNeedValue(task.getScore());
        userGodTrainingTask.setName("宝箱" + task.getId());
        userGodTrainingTask.setDays(0);
        return userGodTrainingTask;
    }

    /**
     * 增加进度
     *
     * @param addValue
     */
    public void addValue(long addValue) {
        this.value += addValue;
        if (this.value >= this.needValue) {
            this.value = this.needValue;
            this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
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
