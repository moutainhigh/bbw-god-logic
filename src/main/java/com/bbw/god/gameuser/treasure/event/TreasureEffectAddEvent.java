package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 法宝效果添加事件
 * @author suhq
 * @date 2018年10月15日 下午2:05:32
 */
public class TreasureEffectAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureEffectAddEvent(EPTreasureEffectAdd source) {
		super(source);
	}


	@Override
	@SuppressWarnings("unchecked")
	public EPTreasureEffectAdd getEP() {
		return (EPTreasureEffectAdd)getSource();
	}
}
