package com.bbw.god.gameuser.task.grow.event;

import com.bbw.common.SpringContextUtil;

public class NewbieTaskEventPublisher {

	public static void pubFinishNewbieTaskEvent(EPFinishNewbieTask dta) {
		SpringContextUtil.publishEvent(new FinishNewbieTaskEvent(dta));
	}
}
