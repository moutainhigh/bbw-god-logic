package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 至宝事件
 *
 * @author: huanghb
 * @date: 2022/9/24 10:51
 */
public class ZhiBaoEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = 1L;

	public ZhiBaoEvent(EPCardZhiBaoAdd source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardZhiBaoAdd getEP() {
		return (EPCardZhiBaoAdd) getSource();
	}
}
