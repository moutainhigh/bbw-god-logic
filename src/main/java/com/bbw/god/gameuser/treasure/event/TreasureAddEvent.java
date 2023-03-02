package com.bbw.god.gameuser.treasure.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 法宝添加事件
 * 
 * @author suhq
 * @date 2018年10月22日 下午1:55:14
 */
public class TreasureAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureAddEvent(EPTreasureAdd source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTreasureAdd getEP() {
		return (EPTreasureAdd) getSource();
	}

}
