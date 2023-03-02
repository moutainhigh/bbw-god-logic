package com.bbw.god.gameuser.shake;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 丢骰子事件
 * @date 2020/2/24 10:55
 */
public class ShakeEvent extends ApplicationEvent implements IEventParam {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public ShakeEvent(EPShake source) {
		super(source);
	}

	@Override
	public EPShake getEP() {
		return (EPShake) getSource();
	}
}
