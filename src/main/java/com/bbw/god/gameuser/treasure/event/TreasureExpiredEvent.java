package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 法宝过期事件
 * @date 2020/6/7 21:09
 **/
public class TreasureExpiredEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -1553822868000449556L;

	public TreasureExpiredEvent(EPTreasureExpired source) {
		super(source);
	}

	/**
	 * 获取事件参数
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EPTreasureExpired getEP() {
		return (EPTreasureExpired) getSource();
	}
}
