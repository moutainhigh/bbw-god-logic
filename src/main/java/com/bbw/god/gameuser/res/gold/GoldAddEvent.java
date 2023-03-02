package com.bbw.god.gameuser.res.gold;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:26
 *
 */
public class GoldAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public GoldAddEvent(EPGoldAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPGoldAdd getEP() {
		return (EPGoldAdd) getSource();
	}

}
