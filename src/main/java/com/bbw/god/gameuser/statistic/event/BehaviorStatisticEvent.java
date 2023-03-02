package com.bbw.god.gameuser.statistic.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 行为统计事件
 * @date 2020/4/18 9:55
 */
public class BehaviorStatisticEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -8772173340897694182L;

	public BehaviorStatisticEvent(EPBehaviorStatistic source) {
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
