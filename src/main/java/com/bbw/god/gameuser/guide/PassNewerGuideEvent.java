package com.bbw.god.gameuser.guide;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suhq
 * @date 2018年11月14日 上午9:30:18
 */
public class PassNewerGuideEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public PassNewerGuideEvent(EPPassNewerGuide source) {
        super(source);
    }

    @Override
    public EPPassNewerGuide getEP() {
        return (EPPassNewerGuide) getSource();
    }
}
