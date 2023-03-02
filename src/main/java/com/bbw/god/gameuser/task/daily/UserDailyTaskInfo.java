package com.bbw.god.gameuser.task.daily;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.UserTaskInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 每日任务信息
 * @date 2020/11/24 10:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDailyTaskInfo extends UserTaskInfo implements Serializable {

    public static UserDailyTaskInfo getInstance(long uid, List<Integer> unFinishIds) {
        UserDailyTaskInfo taskInfo = new UserDailyTaskInfo();
        if (ListUtil.isNotEmpty(unFinishIds)) {
            taskInfo.setUnFinishIds(unFinishIds);
        }
        taskInfo.setGameUserId(uid);
        taskInfo.setId(ID.INSTANCE.nextId());
        return taskInfo;
    }

    public static UserDailyTaskInfo getInstance(long uid, long dataId, List<Integer> unFinishIds) {
        UserDailyTaskInfo taskInfo = new UserDailyTaskInfo();
        if (ListUtil.isNotEmpty(unFinishIds)) {
            taskInfo.setUnFinishIds(unFinishIds);
        }
        taskInfo.setGameUserId(uid);
        taskInfo.setId(dataId);
        return taskInfo;
    }

    public boolean isTodayTask(Integer taskId) {
        if (!isToday()) {
            return false;
        }
        if (this.getUnFinishIds().contains(taskId)) {
            return true;
        }
        if (this.getAccomplishIds().contains(taskId)) {
            return true;
        }
        return this.getAwardedIds().contains(taskId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.DAILY_TASK_INFO;
    }
}
