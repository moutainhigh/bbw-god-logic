package com.bbw.god.city.chengc.in.event;

import com.bbw.god.event.EventParam;
import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.yaozu.event.EPYaoZu;
import org.springframework.context.ApplicationEvent;

/**
 * 法坛升级事件
 *
 * @author fzj
 * @date 2021/11/13 17:26
 */
public class UnlockFaTanEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public UnlockFaTanEvent(EPUnlockFaTan source) {
        super(source);
    }

    @Override
    public EPUnlockFaTan getEP() {
        return (EPUnlockFaTan) getSource();
    }
}
