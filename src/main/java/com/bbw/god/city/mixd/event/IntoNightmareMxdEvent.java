package com.bbw.god.city.mixd.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-12
 */
public class IntoNightmareMxdEvent extends ApplicationEvent implements IEventParam{

    private static final long serialVersionUID = 1L;

    public IntoNightmareMxdEvent(BaseEventParam source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BaseEventParam getEP() {
        return (BaseEventParam)getSource();
    }
}
