package com.bbw.god.gameuser.treasure.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 法宝记录重置事件
 * 
 * @author suhq
 * @date 2019年3月20日 下午2:03:40
 */
public class TreasureRecordResetEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureRecordResetEvent(EPTreasureRecordReset source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTreasureRecordReset getEP() {
		return (EPTreasureRecordReset) getSource();
	}

}
