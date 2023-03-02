package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * value - String 101,1000 - 卡牌ID，增加经验
 * 
 * @author suhq
 * @date 2018年10月9日 下午2:49:33
 */
public class UserCardExpAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardExpAddEvent(EPCardExpAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardExpAdd getEP() {
		return (EPCardExpAdd) this.getSource();
	}

}
