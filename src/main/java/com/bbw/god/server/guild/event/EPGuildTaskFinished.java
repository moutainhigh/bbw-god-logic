package com.bbw.god.server.guild.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EPGuildTaskFinished extends BaseEventParam{
	private Integer taskbaseId;// 基础任务Id
	private Integer edNumber;// 八卦字

	public static EPGuildTaskFinished instance(BaseEventParam baseEp, int baseId, int edNumber) {
		EPGuildTaskFinished ev = new EPGuildTaskFinished();
		ev.setValues(baseEp);
		ev.setTaskbaseId(baseId);
		ev.setEdNumber(edNumber);
		return ev;
	}
}
