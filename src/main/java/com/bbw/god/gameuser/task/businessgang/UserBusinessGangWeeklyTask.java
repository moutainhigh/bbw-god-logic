package com.bbw.god.gameuser.task.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家商帮每周任务
 *
 * @author fzj
 * @date 2022/1/14 14:17
 */
@Data
public class UserBusinessGangWeeklyTask extends UserTask implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer group;
    /** 任务生成时间 */
    private Date generateTime;
    /** 任务剩余时间 */
    private long remainTime;

    public static UserBusinessGangWeeklyTask getInstance(long uid, TaskGroupEnum taskGroup, CfgTaskEntity task) {
        UserBusinessGangWeeklyTask ut = new UserBusinessGangWeeklyTask();
        ut.setId(ID.INSTANCE.nextId());
        ut.setGameUserId(uid);
        ut.setGroup(taskGroup.getValue());
        ut.setBaseId(task.getId());
        ut.setNeedValue(task.getValue());
        ut.setStatus(TaskStatusEnum.DOING.getValue());
        ut.setGenerateTime(DateUtil.now());
        return ut;
    }
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_BUSINESS_GANG_WEEKLY_TASK;
    }
}
