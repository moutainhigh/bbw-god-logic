package com.bbw.god.login.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.login.LoginInfo;
import com.bbw.god.login.RDGameUser;
import lombok.Data;

/**
 * 每日首次登录事件参数
 *
 * @author suhq
 * @date 2019-08-20 09:48:40
 */
@Data
public class EPFirstLoginPerDay extends BaseEventParam {
	private long uid;
	private LoginInfo loginInfo;
	private RDGameUser rdGameUser;

	public EPFirstLoginPerDay(long uid, LoginInfo loginInfo, BaseEventParam bep) {
		this.uid = uid;
		this.loginInfo = loginInfo;
		setValues(bep);
	}

	public EPFirstLoginPerDay(long uid, LoginInfo loginInfo, RDGameUser rdGameUser, BaseEventParam bep) {
		this.uid = uid;
		this.loginInfo = loginInfo;
		this.rdGameUser = rdGameUser;
		setValues(bep);
	}
}
