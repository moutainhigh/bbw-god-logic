package com.bbw.god.gameuser.task.timelimit.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.TaskGroupEnum;

/**
 * 限时任务事件发布器
 *
 * @author: suhq
 * @date: 2021/8/20 11:53 上午
 */
public class TimeLimitTaskEventPublisher {

	public static void pubTimeLimitTaskAchievedEvent(long uid, TaskGroupEnum taskGroup, int taskId) {
		BaseEventParam bep = new BaseEventParam(uid);
		EPTimeLimitTask ep = new EPTimeLimitTask(taskGroup, taskId, bep);
		SpringContextUtil.publishEvent(new TimeLimitTaskAchievedEvent(ep));
	}
}
