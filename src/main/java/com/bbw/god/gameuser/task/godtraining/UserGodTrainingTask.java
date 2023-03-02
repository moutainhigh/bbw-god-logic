package com.bbw.god.gameuser.task.godtraining;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskConfig;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author suchaobin
 * @description 上仙试炼任务
 * @date 2021/1/19 20:39
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UserGodTrainingTask extends UserTask implements Serializable {
    private Long generateTime = DateUtil.toDateTimeLong();// 任务生成时间yyyyMMddHHmmss
    private Integer taskType = TaskTypeEnum.DAILY_TASK.getValue();
    private Integer days;
    private Integer awardIndex;

    public static UserGodTrainingTask fromTask(long guId, CfgTaskEntity task) {
        UserGodTrainingTask trainingTask = new UserGodTrainingTask();
        trainingTask.setId(ID.INSTANCE.nextId());
        trainingTask.setGameUserId(guId);
        trainingTask.setBaseId(task.getId());
        trainingTask.setNeedValue(task.getValue());
        trainingTask.setName(task.getName());
        trainingTask.setDays(task.getDays());
        return trainingTask;
    }

    public static UserGodTrainingTask fromTask(long guId, CfgTaskConfig.CfgBox task) {
        UserGodTrainingTask trainingTask = new UserGodTrainingTask();
        trainingTask.setId(ID.INSTANCE.nextId());
        trainingTask.setGameUserId(guId);
        trainingTask.setBaseId(task.getId());
        trainingTask.setNeedValue(task.getScore());
        trainingTask.setName("宝箱" + task.getId());
        trainingTask.setDays(0);
        return trainingTask;
    }

    public boolean ifValid() {
        Date beginTime = DateUtil.fromDateLong(this.getGenerateTime());
        // 第一天限时任务
        if (95701 == this.getBaseId()) {
            long hourBetween = DateUtil.getHourBetween(beginTime, DateUtil.now());
            return hourBetween < 24;
        }
        // 第2-7天的限时任务
        if (GodTrainingTaskService.TIME_LIMITED_TASKS.contains(this.getBaseId())) {
            int daysBetween = DateUtil.getDaysBetween(beginTime, DateUtil.now());
            return daysBetween < this.getBaseId() % 10;
        }
        return true;
    }


    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.GOD_TRAINING_TASK;
    }
}
