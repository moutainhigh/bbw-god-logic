package com.bbw.god.gameuser.statistic.behavior.login;

import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import org.springframework.context.ApplicationEvent;

/**
 * 登录行为统计事件
 *
 * @author: suhq
 * @date: 2021/7/28 3:54 下午
 */
public class LoginBehaviorStatisticEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = 641211231837855364L;

	public LoginBehaviorStatisticEvent(EPBehaviorStatistic source) {
		super(source);
	}

	/**
	 * 获取事件参数
	 *
	 * @return 事件参数
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EPBehaviorStatistic getEP() {
		return (EPBehaviorStatistic) getSource();
	}
}
