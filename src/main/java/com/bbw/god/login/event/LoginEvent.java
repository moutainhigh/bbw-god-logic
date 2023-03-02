package com.bbw.god.login.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.login.LoginPlayer;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-10 14:48
 */
public class LoginEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 */
	public LoginEvent(LoginPlayer player) {
		super(player);
	}

	public LoginPlayer getLoginPlayer() {
		return (LoginPlayer) this.getSource();
	}
}
