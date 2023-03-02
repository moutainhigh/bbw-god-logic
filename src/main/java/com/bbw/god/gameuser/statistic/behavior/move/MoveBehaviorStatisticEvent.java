package com.bbw.god.gameuser.statistic.behavior.move;

import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import org.springframework.context.ApplicationEvent;

/**
 * 移动行为事件
 *
 * @author: suhq
 * @date: 2021/7/28 2:58 下午
 */
public class MoveBehaviorStatisticEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = -2874956476043641912L;

	public MoveBehaviorStatisticEvent(EPBehaviorStatistic source) {
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
