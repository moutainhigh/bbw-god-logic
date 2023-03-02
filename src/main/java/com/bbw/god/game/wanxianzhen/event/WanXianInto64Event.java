package com.bbw.god.game.wanxianzhen.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
public class WanXianInto64Event extends ApplicationEvent implements IEventParam {

    public WanXianInto64Event(EPWanXianInto64 source) {
        super(source);
    }

    @Override
    public EPWanXianInto64 getEP() {
        return (EPWanXianInto64)getSource();
    }
}
