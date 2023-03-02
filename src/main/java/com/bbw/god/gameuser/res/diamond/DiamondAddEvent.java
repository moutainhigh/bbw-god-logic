package com.bbw.god.gameuser.res.diamond;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 钻石添加事件
 *
 * @author: huanghb
 * @date: 2022/6/15 17:11
 */
public class DiamondAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public DiamondAddEvent(EPDiamondAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDiamondAdd getEP() {
		return (EPDiamondAdd) getSource();
	}

}
