package com.bbw.god.gameuser.res.ele;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 元素添加事件
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:11
 * 
 */
public class EleAddEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = 1L;

	public EleAddEvent(EPEleAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPEleAdd getEP() {
		return (EPEleAdd) getSource();
	}

}
