package com.bbw.god.gameuser.level;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:26
 *
 */
public class GuLevelUpEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public GuLevelUpEvent(EPGuLevelUp ep) {
		super(ep);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPGuLevelUp getEP() {
		return (EPGuLevelUp) getSource();
	}

}
