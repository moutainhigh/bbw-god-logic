package com.bbw.god.gameuser.task.businessgang.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import lombok.Data;

/**
 * 商帮任务参数
 *
 * @author fzj
 * @date 2022/1/29 13:37
 */
@Data
public class EPBusinessGangTask extends BaseEventParam {
    /** 任务iD */
    private int taskId;
    /** 任务类别 */
    private TaskGroupEnum taskGroup;

    public EPBusinessGangTask(int taskId, TaskGroupEnum taskGroup, BaseEventParam bep) {
        setValues(bep);
        this.taskId = taskId;
        this.taskGroup = taskGroup;
    }
}
