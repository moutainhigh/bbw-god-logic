package com.bbw.god.gameuser.chamberofcommerce.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EPTaskFinished extends BaseEventParam{
	private Integer level = 0;// 任务等级 1初 2中 3高
	private String description;
	private String broadcast = null;

	public static EPTaskFinished instance(BaseEventParam ep,int level, String description) {
		EPTaskFinished ev = new EPTaskFinished();
		ev.setLevel(level);
		ev.setValues(ep);
		ev.setDescription(description);
		return ev;
	}

	public static EPTaskFinished instance(BaseEventParam ep, int level, String description, String broadcast) {
		EPTaskFinished ev = new EPTaskFinished();
		ev.setLevel(level);
		ev.setValues(ep);
		ev.setDescription(description);
		ev.setBroadcast(broadcast);
		return ev;
	}

}
