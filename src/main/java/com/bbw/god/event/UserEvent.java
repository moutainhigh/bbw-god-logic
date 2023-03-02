package com.bbw.god.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import com.bbw.god.login.LoginPlayer;

/**
 * 用户基础事件
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-12 15:58
 */

public abstract class UserEvent extends ApplicationEvent {
	private static final long serialVersionUID = -3636955123791233608L;
	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @param source
	 */
	public UserEvent(LoginPlayer user) {
		super(user);
	}

	public LoginPlayer getUser() {
		return (LoginPlayer) this.getSource();
	}

}
