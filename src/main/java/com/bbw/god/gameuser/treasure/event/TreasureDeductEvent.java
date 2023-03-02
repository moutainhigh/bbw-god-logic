package com.bbw.god.gameuser.treasure.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 法宝扣除事件
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:31
 *
 */
public class TreasureDeductEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = 1L;

	public TreasureDeductEvent(EPTreasureDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTreasureDeduct getEP() {
		return (EPTreasureDeduct) getSource();
	}

}
