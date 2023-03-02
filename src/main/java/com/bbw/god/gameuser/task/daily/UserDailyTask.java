package com.bbw.god.gameuser.task.daily;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日任务
 *
 * @author suhq 2018年10月9日 上午9:23:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Deprecated
public class UserDailyTask extends UserTask implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long generateTime;// 任务生成时间yyyyMMddHHmmss
    private Integer taskType = TaskTypeEnum.DAILY_TASK.getValue();
    private Integer awardIndex = null;

    public static UserDailyTask instance(long guId, int taskId, int needValue, long generateTime) {
        UserDailyTask udTask = new UserDailyTask();
        udTask.setId(ID.INSTANCE.nextId());
        udTask.setGameUserId(guId);
        udTask.setBaseId(taskId);
        udTask.setNeedValue(needValue);
        udTask.setGenerateTime(generateTime);
        return udTask;
    }

    public boolean ifTodayTask() {
        return Integer.valueOf(generateTime.toString().substring(0, 8)) == DateUtil.getTodayInt();
    }

    public boolean heroBackValidTask() {
        if (taskType.equals(TaskTypeEnum.HERO_BACK_TASK.getValue())
                || taskType.equals(TaskTypeEnum.HERO_BACK_BOX_TASK.getValue())) {
            Date date = DateUtil.fromDateLong(generateTime);
            long interval = DateUtil.getSecondsBetween(date, new Date());
            long days10seconds = 10 * 60 * 60 * 24;
            return interval < days10seconds;
        }
        return false;
    }

    /**
     * 是否在某一天前（包含这一天）
     *
     * @param dateInt
     * @return
     */
    public boolean ifBefore(int dateInt) {
        return Integer.valueOf(generateTime.toString().substring(0, 8)) <= dateInt;
    }

    public boolean isNomalDailyTask() {
        return taskType.intValue() == TaskTypeEnum.DAILY_TASK.getValue();
    }

    public boolean delHerobackTask() {
        //15天删除一次 因为任务有效期是10天
        if (taskType.intValue() != TaskTypeEnum.HERO_BACK_TASK.getValue()) {
            return false;
        }
        Date date1 = DateUtil.fromDateLong(generateTime);
        int days = DateUtil.getDaysBetween(date1, new Date());
        days = days > 0 ? days : 0 - days;
        return days >= 15;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.DAILY_TASK;
    }

}
