package com.bbw.god.gameuser.special.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 特产加锁事件
 * 
 * @author suhq
 * @date 2018年10月23日 上午8:59:57
 */
public class SpecialLockEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public SpecialLockEvent(EPPocketSpecial source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPPocketSpecial getEP() {
		return (EPPocketSpecial) getSource();
	}

}
