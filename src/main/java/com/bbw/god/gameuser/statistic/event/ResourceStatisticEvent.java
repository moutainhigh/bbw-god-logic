package com.bbw.god.gameuser.statistic.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 资源统计事件
 * @date 2020/4/18 9:55
 */
public class ResourceStatisticEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 8746567336658308692L;

    public ResourceStatisticEvent(EPResourceStatistic source) {
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
