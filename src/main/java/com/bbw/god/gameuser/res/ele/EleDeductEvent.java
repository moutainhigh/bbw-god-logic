package com.bbw.god.gameuser.res.ele;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 元素扣除事件
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:19
 *
 */
public class EleDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public EleDeductEvent(EPEleDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPEleDeduct getEP() {
		return (EPEleDeduct) getSource();
	}

}
