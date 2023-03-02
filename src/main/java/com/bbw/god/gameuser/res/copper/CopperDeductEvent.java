package com.bbw.god.gameuser.res.copper;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:31
 *
 */
public class CopperDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public CopperDeductEvent(EPCopperDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCopperDeduct getEP() {
		return (EPCopperDeduct) getSource();
	}

}
