package com.bbw.god.gameuser.guide;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suhq
 * @date 2018年11月14日 上午9:30:18
 */
public class LogNewerGuideEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public LogNewerGuideEvent(EPLogNewerGuide source) {
        super(source);
    }

    @Override
    public EPLogNewerGuide getEP() {
        return (EPLogNewerGuide) getSource();
    }
}
