package com.bbw.god.rechargeactivities.wartoken.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-03
 */
public class WarTokenActiveEvent extends ApplicationEvent implements IEventParam {

    public WarTokenActiveEvent(BaseEventParam eventParam) {
        super(eventParam);
    }

    @Override
    public BaseEventParam getEP() {
        return (BaseEventParam)getSource();
    }
}
