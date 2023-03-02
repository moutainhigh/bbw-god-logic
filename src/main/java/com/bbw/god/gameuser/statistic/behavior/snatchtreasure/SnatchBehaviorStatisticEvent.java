package com.bbw.god.gameuser.statistic.behavior.snatchtreasure;

import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import org.springframework.context.ApplicationEvent;

/**
 * 夺宝行为事件
 *
 * @author: suhq
 * @date: 2021/7/28 2:58 下午
 */
public class SnatchBehaviorStatisticEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -8772173340897694182L;

	public SnatchBehaviorStatisticEvent(EPBehaviorStatistic source) {
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
