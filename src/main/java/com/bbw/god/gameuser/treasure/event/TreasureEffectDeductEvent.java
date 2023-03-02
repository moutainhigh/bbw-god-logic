package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 法宝生效扣除步数事件
 * 
 * @author suhq
 * @date 2018年11月2日 下午3:50:00
 */
public class TreasureEffectDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureEffectDeductEvent(EPTreasureEffectDeduct source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPTreasureEffectDeduct getEP() {
		return (EPTreasureEffectDeduct)getSource();
	}
}
