package com.bbw.god.gameuser.task.godtraining.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 试炼任务事件发布器
 * @date 2021/1/21 16:22
 **/
public class GodTrainingTaskEventPublisher {
    public static void pubAddGodTrainingPointEvent(Integer point, BaseEventParam bep) {
        SpringContextUtil.publishEvent(new GodTrainingTaskAddPointEvent(new EPGodTrainingTaskAddPoint(point, bep)));
    }
}
