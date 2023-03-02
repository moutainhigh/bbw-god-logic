package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 押押乐下竞猜失败
 * @date 2020/2/28 10:06
 */
public class YaYaLeFailEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 7742063824971371717L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public YaYaLeFailEvent(EPYaYaLeFail source) {
		super(source);
	}

	@Override
	public EPYaYaLeFail getEP() {
		return (EPYaYaLeFail)getSource();
	}
}
