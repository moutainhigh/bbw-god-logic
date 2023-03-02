package com.bbw.god.gameuser.businessgang.digfortreasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 挖宝事件
 *
 * @author fzj
 * @date 2022/1/29 13:36
 */
public class DigTreasureEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public DigTreasureEvent(EPDigTreasure dta) {
        super(dta);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPDigTreasure getEP() {
        return (EPDigTreasure) getSource();
    }
}
