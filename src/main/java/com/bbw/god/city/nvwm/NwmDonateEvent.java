package com.bbw.god.city.nvwm;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 女娲庙捐献事件
 *
 * @author suhq
 * @date 2019-09-18 10:59:52
 */
public class NwmDonateEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public NwmDonateEvent(EPNvWMDonate param) {
		super(param);
	}

	@Override
	public EPNvWMDonate getEP() {
		return (EPNvWMDonate) getSource();
	}
}
