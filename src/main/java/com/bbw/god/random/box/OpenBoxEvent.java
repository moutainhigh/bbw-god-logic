package com.bbw.god.random.box;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 宝箱开启事件
 * @date 2020/2/21 9:32
 */
public class OpenBoxEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public OpenBoxEvent(EPOpenBox param) {
		super(param);
	}

	@Override
	public EPOpenBox getEP() {
		return (EPOpenBox) getSource();
	}
}
