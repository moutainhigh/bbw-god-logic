package com.bbw.god.login.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 每天第一次登陆
 *
 * @author suhq
 * @date 2019年3月6日 下午2:59:59
 */
public class FirstLoginPerDayEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public FirstLoginPerDayEvent(EPFirstLoginPerDay source) {
		super(source);
	}

	/**
	 * 获取事件参数
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EPFirstLoginPerDay getEP() {
		return (EPFirstLoginPerDay) getSource();
	}
}
