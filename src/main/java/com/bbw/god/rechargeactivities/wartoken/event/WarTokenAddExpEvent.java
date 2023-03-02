package com.bbw.god.rechargeactivities.wartoken.event;

import com.bbw.god.event.IEventParam;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-03
 */
public class WarTokenAddExpEvent extends ApplicationEvent implements IEventParam {

    public WarTokenAddExpEvent(EPWarTokenAddExp eventParam) {
        super(eventParam);
    }

    @Override
    public EPWarTokenAddExp getEP() {
        return (EPWarTokenAddExp)getSource();
    }
}
