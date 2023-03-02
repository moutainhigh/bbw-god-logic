package com.bbw.god.city.yeg.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 开启野怪宝箱事件
 * @date 2020/5/9 11:15
 **/
public class OpenYeGuaiBoxEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -2291576289141664995L;

	public OpenYeGuaiBoxEvent(EPOpenYeGuaiBox source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPOpenYeGuaiBox getEP() {
		return (EPOpenYeGuaiBox) getSource();
	}
}
