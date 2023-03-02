package com.bbw.god.gameuser.task;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;


/**
 * 任务信息
 *
 * @author fzj
 * @date 2021/12/29 14:23
 */
@Data
public class RDTaskInfo extends RDSuccess {
    RDTaskItem taskItem;
}
