package com.bbw.god.gameuser.task.timelimit.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import lombok.Data;

/**
 * 限时任务统计
 *
 * @author: suhq
 * @date: 2021/8/20 11:50 上午
 */
@Data
public class EPTimeLimitTask extends BaseEventParam {
	private TaskGroupEnum taskGroup;
	private int taskId;

	public EPTimeLimitTask(TaskGroupEnum taskGroup, int taskId, BaseEventParam bep) {
		setValues(bep);
		this.taskGroup = taskGroup;
		this.taskId = taskId;
	}
}
