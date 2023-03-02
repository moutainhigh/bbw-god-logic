package com.bbw.god.gameuser.task.timelimit.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 限时任务达成事件
 *
 * @author: suhq
 * @date: 2021/8/20 12:54 下午
 */
public class TimeLimitTaskAchievedEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TimeLimitTaskAchievedEvent(EPTimeLimitTask dta) {
		super(dta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTimeLimitTask getEP() {
		return (EPTimeLimitTask) getSource();
	}

}
