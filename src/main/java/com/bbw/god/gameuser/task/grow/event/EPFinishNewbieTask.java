package com.bbw.god.gameuser.task.grow.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

@Data
public class EPFinishNewbieTask extends BaseEventParam{
	// 更新后的值
	private Integer step;
	private String stepName;

	public EPFinishNewbieTask(int step, String stepName, BaseEventParam bep) {
		setValues(bep);
		this.step = step;
		this.stepName = stepName;
	}
}
