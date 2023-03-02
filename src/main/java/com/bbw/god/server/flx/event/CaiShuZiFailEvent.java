package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 猜数字失败事件
 * @date 2020/2/24 15:08
 */
public class CaiShuZiFailEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 5145140564141994846L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public CaiShuZiFailEvent(EPCaiShuZiFail source) {
		super(source);
	}

	@Override
	public EPCaiShuZiFail getEP() {
		return (EPCaiShuZiFail) getSource();
	}
}
