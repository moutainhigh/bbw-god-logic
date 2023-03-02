package com.bbw.god.city.mixd;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 出迷仙洞事件
 *
 * @author suhq
 * @date 2019-05-23 14:56:42
 */
public class OutMxdEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -6372909749680393381L;

	public OutMxdEvent(EPOutMxd source) {
		super(source);
	}

	@Override
	public EPOutMxd getEP() {
		return (EPOutMxd) getSource();
	}
}
