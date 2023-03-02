package com.bbw.god.gameuser.task.timelimit.cunz.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 村庄任务事件发布器
 *
 * @author: suhq
 * @date: 2021/8/20 11:53 上午
 */
public class CunZTaskEventPublisher {

	public static void pubCunZTaskAchievedEvent(long uid, int taskId, boolean isFirstAchieved) {
		BaseEventParam bep = new BaseEventParam(uid);
		EPCunZTask ep = new EPCunZTask(taskId, isFirstAchieved, bep);
		SpringContextUtil.publishEvent(new CunZTaskAchievedEvent(ep));
	}
}
