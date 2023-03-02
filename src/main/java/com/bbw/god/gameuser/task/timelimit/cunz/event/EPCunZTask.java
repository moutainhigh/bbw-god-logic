package com.bbw.god.gameuser.task.timelimit.cunz.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 村庄任务统计
 *
 * @author: suhq
 * @date: 2021/8/20 11:50 上午
 */
@Data
public class EPCunZTask extends BaseEventParam {
	private int taskId;
	private boolean isFirstAchieved;

	public EPCunZTask(int taskId, boolean isFirstAchieved, BaseEventParam bep) {
		setValues(bep);
		this.taskId = taskId;
		this.isFirstAchieved = isFirstAchieved;
	}
}
