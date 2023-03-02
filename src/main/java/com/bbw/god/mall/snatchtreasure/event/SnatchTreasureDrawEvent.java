package com.bbw.god.mall.snatchtreasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 夺宝抽奖事件
 * @date 2020/6/30 14:49
 **/
public class SnatchTreasureDrawEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 8851933144390638086L;

	public SnatchTreasureDrawEvent(EPSnatchTreasureDraw source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPSnatchTreasureDraw getEP() {
		return (EPSnatchTreasureDraw) getSource();
	}
}
