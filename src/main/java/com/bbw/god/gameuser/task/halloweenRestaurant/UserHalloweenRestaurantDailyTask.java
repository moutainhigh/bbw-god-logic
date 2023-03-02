package com.bbw.god.gameuser.task.halloweenRestaurant;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家万圣餐厅每日任务
 *
 * @author: huanghb
 * @date: 2022/10/14 9:20
 */
@Data
public class UserHalloweenRestaurantDailyTask implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 任务id */
    private int taskId;
    /** 最新一个进度完成时间 */
    private long accomplishTime = 0;
    /** 任务生成时间yyyyMMddHHmmss */
    private Long generateTime = DateUtil.toDateTimeLong();
    /** 任务类别 */
    private Integer taskType = TaskTypeEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK.getValue();
    /** 完成任务需要的进度 */
    private Integer needProgress;
    /** 当前进度 */
    private long progress = 0;
    /** 状态 */
    private Integer status = TaskStatusEnum.DOING.getValue();
    /** 任务名称 */
    private String name;

    public static UserHalloweenRestaurantDailyTask fromTask(CfgTaskEntity task) {
        UserHalloweenRestaurantDailyTask userBrocadeGiftDailyTask = new UserHalloweenRestaurantDailyTask();
        userBrocadeGiftDailyTask.setTaskId(task.getId());
        userBrocadeGiftDailyTask.setNeedProgress(task.getValue());
        userBrocadeGiftDailyTask.setName(task.getName());
        return userBrocadeGiftDailyTask;
    }

    /**
     * 增加进度
     *
     * @param addProgress
     */
    public void addProgress(long addProgress) {
        //修复负进度任务
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(this.taskId);
        int taskProgress = (int) (this.getProgress() - this.getNeedProgress() + cfgTaskEntity.getValue());
        if (0 > taskProgress) {
            this.progress = 0;
            this.needProgress = cfgTaskEntity.getValue();
        }
        this.progress += addProgress;
        if (this.progress >= this.needProgress) {
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
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(this.taskId);
        this.needProgress = cfgTaskEntity.getValue();
        this.status = TaskStatusEnum.DOING.getValue();
        this.generateTime = DateUtil.toDateTimeLong();
    }

    /**
     * 开启下一个进度并且处理奖励
     */
    public void openNextProgressAndHandleAward(List<Award> finalAwards, List<Award> awards) {
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(this.taskId);
        while (this.progress >= this.needProgress) {
            this.needProgress += cfgTaskEntity.getValue();
            finalAwards.addAll(awards);
            this.generateTime = DateUtil.toDateTimeLong();
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
