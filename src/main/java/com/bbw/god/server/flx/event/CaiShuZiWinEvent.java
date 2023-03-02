package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 猜数字事件
 * @date 2020/2/24 15:08
 */
public class CaiShuZiWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 2899605976877010627L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public CaiShuZiWinEvent(EPCaiShuZiWin source) {
		super(source);
	}

	@Override
	public EPCaiShuZiWin getEP() {
		return (EPCaiShuZiWin) getSource();
	}
}
