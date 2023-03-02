package com.bbw.god.login.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.login.LoginInfo;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.RDGameUser;

/**
 * 登陆事件发送器
 * 
 * @author suhq
 * @date 2019年3月6日 下午3:01:35
 */
public class LoginEventPublisher {

	/**
	 * 登录事件
	 * 
	 * @param player
	 */
	public static void pubLoginEvent(LoginPlayer player) {
		SpringContextUtil.publishEvent(new LoginEvent(player));
	}

	/**
	 * 发送每天第一次登陆事件
	 *
	 * @param loginInfo
	 */
	public static void pubFirstLoginEvent(LoginInfo loginInfo, RDGameUser rdGameUser) {
		long uid = loginInfo.getUser().getId();
		BaseEventParam bep = new BaseEventParam(uid);
		EPFirstLoginPerDay ep = new EPFirstLoginPerDay(uid, loginInfo, rdGameUser, bep);
		SpringContextUtil.publishEvent(new FirstLoginPerDayEvent(ep));
	}

}
