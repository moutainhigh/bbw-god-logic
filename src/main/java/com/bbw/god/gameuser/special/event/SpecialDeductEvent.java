package com.bbw.god.gameuser.special.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 特产扣除事件
 * 
 * @author suhq
 * @date 2018年10月23日 上午9:09:11
 */
public class SpecialDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public SpecialDeductEvent(EPSpecialDeduct source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPSpecialDeduct getEP() {
		return (EPSpecialDeduct) getSource();
	}

}
