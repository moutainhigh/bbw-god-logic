package com.bbw.god.gameuser.res.gold;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:31
 *
 */
public class GoldDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public GoldDeductEvent(EPGoldDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPGoldDeduct getEP() {
		return (EPGoldDeduct) getSource();
	}

}
