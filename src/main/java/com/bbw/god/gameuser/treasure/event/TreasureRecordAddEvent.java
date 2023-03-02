package com.bbw.god.gameuser.treasure.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 添加法宝记录事件
 * 
 * @author suhq
 * @date 2018年11月28日 下午3:48:05
 */
public class TreasureRecordAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureRecordAddEvent(EPTreasureRecordAdd source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTreasureRecordAdd getEP() {
		return (EPTreasureRecordAdd) getSource();
	}

}
