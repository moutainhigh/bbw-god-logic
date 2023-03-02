package com.bbw.god.activity.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 活动达成事件
 *
 * @author suhq
 * @date 2019年3月6日 下午2:59:59
 */
public class ActivityAccomplishEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public ActivityAccomplishEvent(EPActivityAccomplish source) {
        super(source);
    }

    @Override
    public EPActivityAccomplish getEP() {
        return (EPActivityAccomplish) getSource();
    }
}
