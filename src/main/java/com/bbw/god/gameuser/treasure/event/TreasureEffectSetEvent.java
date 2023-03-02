package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * value - String - 法宝ID,剩余效果
 * 
 * @author suhq
 * @date 2018年10月15日 下午2:05:32
 */
public class TreasureEffectSetEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureEffectSetEvent(EPTreasureEffectSet source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPTreasureEffectSet  getEP() {
		return (EPTreasureEffectSet)getSource();
	}
}
