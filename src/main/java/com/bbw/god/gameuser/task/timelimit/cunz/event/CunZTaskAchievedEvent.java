package com.bbw.god.gameuser.task.timelimit.cunz.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 村庄任务达成事件(TimeLimitTaskAchievedEvent后触发)
 *
 * @author: suhq
 * @date: 2021/8/20 12:46 下午
 */
public class CunZTaskAchievedEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public CunZTaskAchievedEvent(EPCunZTask dta) {
		super(dta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCunZTask getEP() {
		return (EPCunZTask) getSource();
	}

}
