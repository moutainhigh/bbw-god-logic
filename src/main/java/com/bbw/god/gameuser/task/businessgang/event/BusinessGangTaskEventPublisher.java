package com.bbw.god.gameuser.task.businessgang.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.TaskGroupEnum;

/**
 * 商帮任务事件发布器
 *
 * @author fzj
 * @date 2022/1/29 13:46
 */
public class BusinessGangTaskEventPublisher {

    public static void pubBusinessGangTaskAchievedEvent(long uid, int taskId, TaskGroupEnum taskGroup) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPBusinessGangTask ep = new EPBusinessGangTask(taskId, taskGroup, bep);
        SpringContextUtil.publishEvent(new BusinessGangTaskAchievedEvent(ep));
    }
}
