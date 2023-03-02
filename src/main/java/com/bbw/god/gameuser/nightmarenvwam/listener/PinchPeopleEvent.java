package com.bbw.god.gameuser.nightmarenvwam.listener;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 捏人事件
 *
 * @author: huanghb
 * @date: 2022/5/20 16:34
 */
public class PinchPeopleEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public PinchPeopleEvent(EPPinchPeople source) {
        super(source);
    }

    @Override
    public EPPinchPeople getEP() {
        return (EPPinchPeople) getSource();
    }
}
