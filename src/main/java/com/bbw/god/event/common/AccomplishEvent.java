package com.bbw.god.event.common;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 通用达成事件
 *
 * @author suhq
 * @date 2020-02-12 21:13:58
 */
public class AccomplishEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public AccomplishEvent(EPAccomplish source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPAccomplish getEP() {
        return (EPAccomplish) getSource();
    }

}
