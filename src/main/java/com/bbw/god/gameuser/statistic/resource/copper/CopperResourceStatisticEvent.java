package com.bbw.god.gameuser.statistic.resource.copper;

import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.statistic.event.EPResourceStatistic;
import org.springframework.context.ApplicationEvent;

/**
 * 铜钱资源统计事件
 *
 * @author: suhq
 * @date: 2021/8/1 9:39 下午
 */
public class CopperResourceStatisticEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 8746567336658308692L;

    public CopperResourceStatisticEvent(EPResourceStatistic source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return 事件参数
     */
    @Override
    @SuppressWarnings("unchecked")
    public EPResourceStatistic getEP() {
        return (EPResourceStatistic) getSource();
    }
}
